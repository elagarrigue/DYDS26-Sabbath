package edu.dyds.data.remote

import edu.dyds.data.remote.tmdb.TMDBMovie

@Suppress("unused")
interface MovieDetailsRemoteSource {
	suspend fun searchMovieByTitle(title: String): TMDBMovie?
}


