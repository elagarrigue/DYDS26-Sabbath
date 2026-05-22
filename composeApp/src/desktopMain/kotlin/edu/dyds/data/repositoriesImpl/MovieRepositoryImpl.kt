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
        movieLocalDataSource.saveMovies(remote)
        return remote
    }

    override suspend fun getMovieDetailByTitle(title: String): Movie? {
        movieLocalDataSource.getCachedMovies().firstOrNull { it.title == title }?.let {
            return it
        }

        val remoteDetail = detailsSource.searchMovieByTitle(title)
        if (remoteDetail != null) {
            val current = movieLocalDataSource.getCachedMovies().toMutableList()
            current.add(remoteDetail)
            movieLocalDataSource.saveMovies(current)
            return remoteDetail
        }

        return null
    }
}

