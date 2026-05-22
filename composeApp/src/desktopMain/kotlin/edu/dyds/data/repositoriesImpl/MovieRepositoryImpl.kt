package edu.dyds.data.repositoriesImpl

import edu.dyds.data.local.MovieLocalDataSource
import edu.dyds.data.remote.MovieRemoteDataSource
import edu.dyds.data.remote.toDomainMovie
import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository

class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
) : MovieRepository {
    override suspend fun getMovies(): List<Movie> {
        val cached = movieLocalDataSource.getCachedMovies()
        if (cached.isNotEmpty()) {
            return cached
        }

        val remote = movieRemoteDataSource.getPopularMovies()
        val domainMovies = remote.map { it.toDomainMovie() }
        movieLocalDataSource.saveMovies(domainMovies)
        return domainMovies
    }

    override suspend fun getMovieDetailByTitle(title: String): Movie? {
        movieLocalDataSource.getCachedMovies().firstOrNull { it.title == title }?.let {
            return it
        }

        val remoteDetail = movieRemoteDataSource.searchMovieByTitle(title)
        if (remoteDetail != null) {
            val domainDetail = remoteDetail.toDomainMovie()
            val current = movieLocalDataSource.getCachedMovies().toMutableList()
            current.add(domainDetail)
            movieLocalDataSource.saveMovies(current)
            return domainDetail
        }

        return null
    }
}

