package edu.dyds.data.fakes

import edu.dyds.data.external.PopularMoviesExternalSource
import edu.dyds.domain.entities.Movie

class FakePopularMoviesRemoteSource(
    private val popularMovies: List<Movie> = emptyList(),
) : PopularMoviesExternalSource {

    var getPopularMoviesInvocationCount: Int = 0

    override suspend fun getPopularMovies(): List<Movie> {
        getPopularMoviesInvocationCount++
        return popularMovies
    }
}

