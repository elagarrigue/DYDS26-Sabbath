package edu.dyds.data.fakes

import edu.dyds.data.external.MovieDetailsExternalSource
import edu.dyds.domain.entities.Movie

class FakeThrowingMovieDetailsRemoteSource : MovieDetailsExternalSource {
    var invocationCount: Int = 0

    override suspend fun searchMovieByTitle(title: String): Movie? {
        invocationCount++
        error("boom: $title")
    }
}