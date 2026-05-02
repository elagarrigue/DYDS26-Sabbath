package edu.dyds.data.local

import edu.dyds.data.remote.RemoteMovie

class MovieLocalDataSourceImpl : MovieLocalDataSource {
    private val cache = mutableListOf<RemoteMovie>()
    private val lock = Any()

    override suspend fun getCachedMovies(): List<RemoteMovie> = synchronized(lock) {
        cache.toList()
    }

    override suspend fun saveMovies(movies: List<RemoteMovie>) {
        synchronized(lock) {
            cache.clear()
            cache.addAll(movies)
        }
    }

    override suspend fun getCachedMovieDetail(id: Int): RemoteMovie? = synchronized(lock) {
        cache.find { it.id == id }
    }

    override suspend fun clear() = synchronized(lock) {
        cache.clear()
    }
}


