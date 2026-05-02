package edu.dyds.data.local

import edu.dyds.data.remote.RemoteMovie

interface MovieLocalDataSource {
    suspend fun getCachedMovies(): List<RemoteMovie>
    suspend fun saveMovies(movies: List<RemoteMovie>)
    suspend fun getCachedMovieDetail(id: Int): RemoteMovie?
    suspend fun clear()
}

