package edu.dyds.data.fakes

import edu.dyds.data.remote.tmdb.TMDBMovie
import edu.dyds.data.remote.tmdb.TMDBMoviesRemoteSource

class FakeMovieRemoteDataSource(
    private val popularMovies: List<TMDBMovie> = emptyList(),
    private val movieDetail: TMDBMovie? = null,
) : TMDBMoviesRemoteSource {

    var requestedTitle: String? = null
    var getPopularMoviesInvocationCount: Int = 0
    var searchMovieByTitleInvocationCount: Int = 0

    override suspend fun getPopularMovies(): List<TMDBMovie> {
        getPopularMoviesInvocationCount++
        return popularMovies
    }

    override suspend fun searchMovieByTitle(title: String): TMDBMovie? {
        searchMovieByTitleInvocationCount++
        requestedTitle = title
        return movieDetail
    }
}
