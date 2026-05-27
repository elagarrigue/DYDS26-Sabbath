package edu.dyds.data.external

import edu.dyds.domain.entities.Movie

/**
 * External source interface for fetching movie details by title.
 * Implemented by providers like TMDB and OMDB.
 */
interface MovieDetailsExternalSource : MovieExternalSource {
    suspend fun searchMovieByTitle(title: String): Movie?
}

