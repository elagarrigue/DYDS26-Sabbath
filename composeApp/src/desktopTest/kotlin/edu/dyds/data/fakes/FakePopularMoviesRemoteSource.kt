package edu.dyds.data.fakes

import edu.dyds.data.remote.PopularMoviesRemoteSource
import edu.dyds.data.remote.tmdb.TMDBMovie

class FakePopularMoviesRemoteSource(
    private val popularMovies: List<TMDBMovie> = emptyList(),
) : PopularMoviesRemoteSource {

    var getPopularMoviesInvocationCount: Int = 0

    override suspend fun getPopularMovies(): List<TMDBMovie> {
        getPopularMoviesInvocationCount++
        return popularMovies
    }
}

