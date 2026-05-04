package edu.dyds.data.local

import edu.dyds.domain.entities.Movie

interface MovieLocalDataSource {
    suspend fun getCachedMovies(): List<Movie>
    suspend fun saveMovies(movies: List<Movie>)
    suspend fun getCachedMovieDetail(id: Int): Movie?
}

