package edu.dyds.data.fakes

import edu.dyds.data.local.MovieLocalDataSource
import edu.dyds.domain.entities.Movie

class FakeMovieLocalDataSource(
    cachedMovies: List<Movie> = emptyList(),
) : MovieLocalDataSource {

    private var cachedMoviesState: List<Movie> = cachedMovies
    var saveInvocationCount: Int = 0
    var savedMovies: List<Movie>? = null

    override suspend fun getCachedMovies(): List<Movie> = cachedMoviesState

    override suspend fun saveMovies(movies: List<Movie>) {
        saveInvocationCount++
        savedMovies = movies
        cachedMoviesState = movies
    }

    override suspend fun getCachedMovieDetail(title: String): Movie? {
        return cachedMoviesState.find { it.title == title }
    }
}
