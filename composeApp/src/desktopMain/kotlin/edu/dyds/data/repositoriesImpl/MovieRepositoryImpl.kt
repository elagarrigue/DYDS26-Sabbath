package edu.dyds.data.repositoriesImpl

import edu.dyds.data.local.MovieLocalDataSource
import edu.dyds.data.external.tmdb.TMDBMoviesExternalSource
import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.data.remote.PopularMoviesRemoteSource
import edu.dyds.data.external.PopularMoviesExternalSource
import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository

class MovieRepositoryImpl(
    private val popularMoviesSource: PopularMoviesRemoteSource,
    private val detailsSource: MovieDetailsRemoteSource,
    private val movieLocalDataSource: MovieLocalDataSource,
) : MovieRepository {
    @Suppress("unused")
    constructor(
        movieExternalDataSource: TMDBMoviesExternalSource,
        movieLocalDataSource: MovieLocalDataSource,
    ) : this(
        popularMoviesSource = movieExternalDataSource as PopularMoviesRemoteSource,
        detailsSource = movieExternalDataSource as MovieDetailsRemoteSource,
        movieLocalDataSource = movieLocalDataSource,
    )

    override suspend fun getMovies(): List<Movie> {
        val cached = movieLocalDataSource.getCachedMovies()
        if (cached.isNotEmpty()) {
            return cached
        }

        val remote = runCatching { popularMoviesSource.getPopularMovies() }.getOrElse { emptyList() }
        val sourceMovies = remote.ifEmpty { fallbackMovies() }

        val normalized = sourceMovies.map {
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
        movieLocalDataSource.saveMovies(normalized)
        return normalized
    }

    override suspend fun getMovieDetailByTitle(title: String): Movie? {
        val remoteDetail = runCatching {
            detailsSource.searchMovieByTitle(title)
        }.getOrNull()
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

    private fun fallbackMovies(): List<Movie> {
        return listOf(
            Movie(id = 1, title = "The Godfather", poster = "", voteAverage = 9.2, releaseDate = "1972-03-24", overview = "Fallback local catalog"),
            Movie(id = 2, title = "The Dark Knight", poster = "", voteAverage = 9.0, releaseDate = "2008-07-18", overview = "Fallback local catalog"),
            Movie(id = 3, title = "Pulp Fiction", poster = "", voteAverage = 8.9, releaseDate = "1994-10-14", overview = "Fallback local catalog"),
            Movie(id = 4, title = "Inception", poster = "", voteAverage = 8.8, releaseDate = "2010-07-16", overview = "Fallback local catalog"),
            Movie(id = 5, title = "Fight Club", poster = "", voteAverage = 8.8, releaseDate = "1999-10-15", overview = "Fallback local catalog"),
            Movie(id = 6, title = "Forrest Gump", poster = "", voteAverage = 8.7, releaseDate = "1994-07-06", overview = "Fallback local catalog"),
            Movie(id = 7, title = "Interstellar", poster = "", voteAverage = 8.6, releaseDate = "2014-11-07", overview = "Fallback local catalog"),
            Movie(id = 8, title = "Joker", poster = "", voteAverage = 6.8, releaseDate = "2019-10-04", overview = "Fallback local catalog"),
        )
    }
}

