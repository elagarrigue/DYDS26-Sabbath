package edu.dyds.data.local

import edu.dyds.domain.entities.Movie

class MovieLocalDataSourceImpl : MovieLocalDataSource {
    private val cache = mutableListOf<Movie>()
    private val lock = Any()

    override suspend fun getCachedMovies(): List<Movie> = synchronized(lock) {
        cache.toList()
    }

    override suspend fun saveMovies(movies: List<Movie>) {
        synchronized(lock) {
            cache.clear()
            cache.addAll(movies)
        }
    }

    override suspend fun getCachedMovieDetail(title: String): Movie? = synchronized(lock) {
        cache.find { it.title == title }
    }
}


