package edu.dyds.data.external.tmdb

import edu.dyds.data.external.PopularMoviesExternalSource
import edu.dyds.data.external.MovieDetailsExternalSource

/**
 * TMDB-specific implementation of external movie sources.
 * Provides both popular movies and movie details by title.
 */
interface TMDBMoviesExternalSource : PopularMoviesExternalSource, MovieDetailsExternalSource

