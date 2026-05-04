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

    override suspend fun getMovieDetail(id: Int): Movie? {
        movieLocalDataSource.getCachedMovieDetail(id)?.let {
            return it
        }

        val remoteDetail = movieRemoteDataSource.getMovieDetail(id)
        if (remoteDetail != null) {
            val domainDetail = remoteDetail.toDomainMovie()
            val current = movieLocalDataSource.getCachedMovies().toMutableList()
            val index = current.indexOfFirst { it.id == domainDetail.id }
            if (index >= 0) current[index] = domainDetail else current.add(domainDetail)
            movieLocalDataSource.saveMovies(current)
            return domainDetail
        }

        return null
    }
}

