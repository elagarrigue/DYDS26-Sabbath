package edu.dyds.data.remote.tmdb

import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.data.remote.PopularMoviesRemoteSource
import edu.dyds.data.external.tmdb.TMDBMoviesExternalSource

interface TMDBMoviesRemoteSource : TMDBMoviesExternalSource, PopularMoviesRemoteSource, MovieDetailsRemoteSource





