package edu.dyds.data.external.omdb

import edu.dyds.data.external.MovieDetailsExternalSource

/**
 * OMDB-specific implementation of external movie sources.
 * Provides movie details by title only (no popular movies list).
 */
interface OMDBMoviesExternalSource : MovieDetailsExternalSource

