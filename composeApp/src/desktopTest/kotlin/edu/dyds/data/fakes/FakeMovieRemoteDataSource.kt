package edu.dyds.data.fakes

import edu.dyds.data.remote.MovieRemoteDataSource
import edu.dyds.data.remote.RemoteMovie

class FakeMovieRemoteDataSource(
    private val popularMovies: List<RemoteMovie> = emptyList(),
    private val movieDetail: RemoteMovie? = null,
) : MovieRemoteDataSource {

    var requestedTitle: String? = null
    var getPopularMoviesInvocationCount: Int = 0
    var searchMovieByTitleInvocationCount: Int = 0

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        getPopularMoviesInvocationCount++
        return popularMovies
    }

    override suspend fun searchMovieByTitle(title: String): RemoteMovie? {
        searchMovieByTitleInvocationCount++
        requestedTitle = title
        return movieDetail
    }
}
