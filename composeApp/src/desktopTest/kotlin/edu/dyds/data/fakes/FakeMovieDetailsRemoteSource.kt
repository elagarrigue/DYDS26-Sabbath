package edu.dyds.data.fakes

import edu.dyds.data.external.MovieDetailsExternalSource
import edu.dyds.domain.entities.Movie

class FakeMovieDetailsRemoteSource(
    private val movieDetail: Movie? = null,
) : MovieDetailsExternalSource {

    var searchMovieByTitleInvocationCount: Int = 0
    var requestedTitle: String? = null

    override suspend fun searchMovieByTitle(title: String): Movie? {
        searchMovieByTitleInvocationCount++
        requestedTitle = title
        return movieDetail
    }
}

