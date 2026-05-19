package edu.dyds.data.fakes

import edu.dyds.data.remote.MovieRemoteDataSource
import edu.dyds.data.remote.RemoteMovie

class FakeMovieRemoteDataSource(
    private val popularMovies: List<RemoteMovie> = emptyList(),
    private val movieDetail: RemoteMovie? = null,
) : MovieRemoteDataSource {

    var requestedId: Int? = null
    var getPopularMoviesInvocationCount: Int = 0
    var getMovieDetailInvocationCount: Int = 0

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        getPopularMoviesInvocationCount++
        return popularMovies
    }

    override suspend fun getMovieDetail(id: Int): RemoteMovie? {
        getMovieDetailInvocationCount++
        requestedId = id
        return movieDetail
    }
}
