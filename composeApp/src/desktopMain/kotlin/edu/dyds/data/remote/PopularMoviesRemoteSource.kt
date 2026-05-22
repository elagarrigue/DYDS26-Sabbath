package edu.dyds.data.remote

import edu.dyds.data.remote.tmdb.TMDBMovie

@Suppress("unused")
interface PopularMoviesRemoteSource {
    suspend fun getPopularMovies(): List<TMDBMovie>
}

