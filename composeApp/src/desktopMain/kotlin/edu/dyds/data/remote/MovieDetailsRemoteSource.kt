package edu.dyds.data.remote

import edu.dyds.domain.entities.Movie

@Suppress("unused")
interface MovieDetailsRemoteSource {
	suspend fun searchMovieByTitle(title: String): Movie?
}


