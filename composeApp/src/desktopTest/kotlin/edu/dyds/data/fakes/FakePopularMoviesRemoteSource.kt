package edu.dyds.data.fakes

import edu.dyds.data.remote.PopularMoviesRemoteSource
import edu.dyds.domain.entities.Movie

class FakePopularMoviesRemoteSource(
    private val popularMovies: List<Movie> = emptyList(),
) : PopularMoviesRemoteSource {

    var getPopularMoviesInvocationCount: Int = 0

    override suspend fun getPopularMovies(): List<Movie> {
        getPopularMoviesInvocationCount++
        return popularMovies
    }
}

