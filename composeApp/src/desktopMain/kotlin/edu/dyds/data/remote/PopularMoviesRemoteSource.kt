package edu.dyds.data.remote

import edu.dyds.domain.entities.Movie

@Suppress("unused")
interface PopularMoviesRemoteSource {
    suspend fun getPopularMovies(): List<Movie>
}

