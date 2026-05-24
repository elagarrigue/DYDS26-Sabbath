package edu.dyds.data.remote

import edu.dyds.domain.entities.Movie

@Suppress("unused")
interface MovieDetailsRemoteSource : MovieExternalSource {
    override suspend fun searchMovieByTitle(title: String): Movie?
}


