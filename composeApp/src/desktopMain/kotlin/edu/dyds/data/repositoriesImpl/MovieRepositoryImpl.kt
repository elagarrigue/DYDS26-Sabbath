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
        // Cache-first: try local cache
        val cached = movieLocalDataSource.getCachedMovies()
        if (cached.isNotEmpty()) {
            return cached.map { it.toDomainMovie() }
        }

        // Fallback to remote and store in local cache
        val remote = movieRemoteDataSource.getPopularMovies()
        movieLocalDataSource.saveMovies(remote)
        return remote.map { it.toDomainMovie() }
    }

    override suspend fun getMovieDetail(id: Int): Movie? {
        // Try local cache first
        movieLocalDataSource.getCachedMovieDetail(id)?.let {
            return it.toDomainMovie()
        }

        // Fetch remote, then add to cache if present
        val remoteDetail = movieRemoteDataSource.getMovieDetail(id)
        if (remoteDetail != null) {
            // merge into cache: get current list and add/replace the item
            val current = movieLocalDataSource.getCachedMovies().toMutableList()
            val index = current.indexOfFirst { it.id == remoteDetail.id }
            if (index >= 0) current[index] = remoteDetail else current.add(remoteDetail)
            movieLocalDataSource.saveMovies(current)
        }

        return remoteDetail?.toDomainMovie()
    }
}

