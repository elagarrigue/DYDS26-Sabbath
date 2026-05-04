package edu.dyds.domain.usecases

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetMoviesUseCaseImplTest {

    @Test
    fun `invoke returns movies sorted by popularity descending`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(
                movie(id = 1, popularity = 10.0, voteAverage = 6.0),
                movie(id = 2, popularity = 50.0, voteAverage = 7.0),
                movie(id = 3, popularity = 30.0, voteAverage = 8.0),
            )
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `invoke marks movie as good when vote average is greater than seven`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 8.5))
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertTrue(result.single().isGoodMovie)
    }

    @Test
    fun `invoke marks movie as good when vote average is exactly seven`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 7.0))
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertTrue(result.single().isGoodMovie)
    }

    @Test
    fun `invoke marks movie as not good when vote average is lower than seven`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 6.9))
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertFalse(result.single().isGoodMovie)
    }

    @Test
    fun `invoke returns empty list when repository has no movies`() = runTest {
        val repository = FakeMovieRepository(movies = emptyList())
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertTrue(result.isEmpty())
    }

    private class FakeMovieRepository(
        private val movies: List<Movie>,
    ) : MovieRepository {

        override suspend fun getMovies(): List<Movie> = movies

        override suspend fun getMovieDetail(id: Int): Movie? = null
    }

    private fun movie(
        id: Int,
        popularity: Double,
        voteAverage: Double,
    ) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
        popularity = popularity,
        voteAverage = voteAverage,
    )
}
