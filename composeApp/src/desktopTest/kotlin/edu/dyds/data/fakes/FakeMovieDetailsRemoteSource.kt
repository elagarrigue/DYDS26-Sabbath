package edu.dyds.data.fakes

import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.data.remote.tmdb.TMDBMovie

class FakeMovieDetailsRemoteSource(
    private val movieDetail: TMDBMovie? = null,
) : MovieDetailsRemoteSource {

    var searchMovieByTitleInvocationCount: Int = 0
    var requestedTitle: String? = null

    override suspend fun searchMovieByTitle(title: String): TMDBMovie? {
        searchMovieByTitleInvocationCount++
        requestedTitle = title
        return movieDetail
    }
}

