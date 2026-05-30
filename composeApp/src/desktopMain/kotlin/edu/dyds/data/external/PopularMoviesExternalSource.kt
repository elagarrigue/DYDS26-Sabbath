package edu.dyds.data.external

import edu.dyds.domain.entities.Movie

/**
 * External source interface for fetching popular movies.
 * Implemented by providers like TMDB.
 */
interface PopularMoviesExternalSource {
    suspend fun getPopularMovies(): List<Movie>
}

