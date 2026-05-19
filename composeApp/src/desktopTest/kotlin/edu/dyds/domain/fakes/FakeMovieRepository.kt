package edu.dyds.domain.fakes

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository

class FakeMovieRepository(
    private val movies: List<Movie> = emptyList(),
    private val movieDetail: Movie? = null,
) : MovieRepository {

    var requestedId: Int? = null
    var getMoviesInvocationCount: Int = 0
    var getMovieDetailInvocationCount: Int = 0

    override suspend fun getMovies(): List<Movie> {
        getMoviesInvocationCount++
        return movies
    }

    override suspend fun getMovieDetail(id: Int): Movie? {
        getMovieDetailInvocationCount++
        requestedId = id
        return movieDetail
    }
}
