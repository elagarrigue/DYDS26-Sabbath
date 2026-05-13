package edu.dyds.domain.usecases

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.fakes.FakeMovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetMoviesUseCaseImplTest {

    @Test
    fun `when repository returns movies with different popularity, invoke sorts them descending by popularity`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(
                movie(id = 1, popularity = 10.0, voteAverage = 6.0),
                movie(id = 2, popularity = 50.0, voteAverage = 7.0),
                movie(id = 3, popularity = 30.0, voteAverage = 8.0),
            )
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertEquals(1, repository.getMoviesInvocationCount)
        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `when movie vote average is greater than seven, invoke marks it as a good movie`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 8.5))
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertEquals(1, repository.getMoviesInvocationCount)
        assertTrue(result.single().isGoodMovie)
    }

    @Test
    fun `when movie vote average is exactly seven, invoke marks it as a good movie`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 7.0))
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertEquals(1, repository.getMoviesInvocationCount)
        assertTrue(result.single().isGoodMovie)
    }

    @Test
    fun `when movie vote average is lower than seven, invoke marks it as not a good movie`() = runTest {
        val repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 6.9))
        )
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertEquals(1, repository.getMoviesInvocationCount)
        assertFalse(result.single().isGoodMovie)
    }

    @Test
    fun `when repository returns no movies, invoke returns an empty list`() = runTest {
        val repository = FakeMovieRepository(movies = emptyList())
        val useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertEquals(1, repository.getMoviesInvocationCount)
        assertTrue(result.isEmpty())
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
