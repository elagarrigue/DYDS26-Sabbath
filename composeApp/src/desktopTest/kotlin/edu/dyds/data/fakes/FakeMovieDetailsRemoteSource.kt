package edu.dyds.data.fakes

import edu.dyds.data.external.MovieDetailsExternalSource
import edu.dyds.domain.entities.Movie

class FakeMovieDetailsRemoteSource(
    private val movie: Movie? = null,
) : MovieDetailsExternalSource {
    var invocationCount: Int = 0
    var requestedTitle: String? = null

    override suspend fun searchMovieByTitle(title: String): Movie? {
        invocationCount++
        requestedTitle = title
        return movie
    }
}

