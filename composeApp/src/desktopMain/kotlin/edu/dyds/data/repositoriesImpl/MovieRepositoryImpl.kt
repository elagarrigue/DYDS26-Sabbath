package edu.dyds.data.repositoriesImpl

import edu.dyds.data.local.MovieLocalDataSource
import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.data.remote.PopularMoviesRemoteSource
import edu.dyds.data.remote.tmdb.TMDBMoviesRemoteSource
import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository

class MovieRepositoryImpl(
    private val popularMoviesSource: PopularMoviesRemoteSource,
    private val detailsSource: MovieDetailsRemoteSource,
    private val movieLocalDataSource: MovieLocalDataSource,
) : MovieRepository {
    constructor(
        movieRemoteDataSource: TMDBMoviesRemoteSource,
        movieLocalDataSource: MovieLocalDataSource,
    ) : this(
        popularMoviesSource = movieRemoteDataSource as PopularMoviesRemoteSource,
        detailsSource = movieRemoteDataSource as MovieDetailsRemoteSource,
        movieLocalDataSource = movieLocalDataSource,
    )

    override suspend fun getMovies(): List<Movie> {
        val cached = movieLocalDataSource.getCachedMovies()
        if (cached.isNotEmpty()) {
            return cached
        }

        val remote = popularMoviesSource.getPopularMovies()
        // Popular movies come from TMDB; store them with a TMDB prefix in the overview so the UI
        // signals the source when the user navigates to detail.
        val prefixed = remote.map {
            Movie(
                id = it.id,
                title = it.title,
                poster = it.poster,
                backdrop = it.backdrop,
                overview = if (it.overview.isBlank()) it.overview else "TMDB: ${it.overview}",
                originalLanguage = it.originalLanguage,
                originalTitle = it.originalTitle,
                popularity = it.popularity,
                releaseDate = it.releaseDate,
                voteAverage = it.voteAverage,
            )
        }
        movieLocalDataSource.saveMovies(prefixed)
        return prefixed
    }

    override suspend fun getMovieDetailByTitle(title: String): Movie? {
        val remoteDetail = detailsSource.searchMovieByTitle(title)
        if (remoteDetail != null) {
            val current = movieLocalDataSource.getCachedMovies().toMutableList()
            val existingIndex = current.indexOfFirst { it.id == remoteDetail.id || it.title == remoteDetail.title }
            if (existingIndex >= 0) {
                current[existingIndex] = remoteDetail
            } else {
                current.add(remoteDetail)
            }
            movieLocalDataSource.saveMovies(current)
            return remoteDetail
        }

        movieLocalDataSource.getCachedMovies().firstOrNull { it.title == title }?.let {
            return it
        }

        return null
    }
}

