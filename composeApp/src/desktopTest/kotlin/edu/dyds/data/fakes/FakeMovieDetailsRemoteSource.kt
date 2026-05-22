package edu.dyds.data.fakes

import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.domain.entities.Movie

class FakeMovieDetailsRemoteSource(
    private val movieDetail: Movie? = null,
) : MovieDetailsRemoteSource {

    var searchMovieByTitleInvocationCount: Int = 0
    var requestedTitle: String? = null

    override suspend fun searchMovieByTitle(title: String): Movie? {
        searchMovieByTitleInvocationCount++
        requestedTitle = title
        return movieDetail
    }
}

