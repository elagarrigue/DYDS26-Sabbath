package edu.dyds.domain.fakes

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository

class FakeMovieRepository(
    private val movies: List<Movie> = emptyList(),
    private val movieDetail: Movie? = null,
) : MovieRepository {

    var requestedTitle: String? = null
    var getMoviesInvocationCount: Int = 0
    var getMovieDetailByTitleInvocationCount: Int = 0

    override suspend fun getMovies(): List<Movie> {
        getMoviesInvocationCount++
        return movies
    }

    override suspend fun getMovieDetailByTitle(title: String): Movie? {
        getMovieDetailByTitleInvocationCount++
        requestedTitle = title
        return movieDetail
    }
}
