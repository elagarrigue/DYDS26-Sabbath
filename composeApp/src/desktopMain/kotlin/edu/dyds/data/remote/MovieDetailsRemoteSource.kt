package edu.dyds.data.remote

import edu.dyds.data.external.MovieDetailsExternalSource
import edu.dyds.domain.entities.Movie

@Suppress("unused")
interface MovieDetailsRemoteSource : MovieDetailsExternalSource {
    override suspend fun searchMovieByTitle(title: String): Movie?
}


