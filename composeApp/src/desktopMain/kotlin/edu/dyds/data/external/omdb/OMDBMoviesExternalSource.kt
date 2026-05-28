package edu.dyds.data.external.omdb

import edu.dyds.data.external.MovieDetailsExternalSource

/**
 * OMDB-specific interface for external movie data sources.
 *
 * OMDB provides movie details by exact title match (no search by popular/trending list).
 * Implements the generic MovieDetailsExternalSource contract for accessing OMDB data.
 */
interface OMDBMoviesExternalSource : MovieDetailsExternalSource

