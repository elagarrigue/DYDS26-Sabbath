package edu.dyds.domain.usecases

import edu.dyds.domain.fakes.FakeMovieRepository
import edu.dyds.domain.entities.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class GetMovieDetailUseCaseImplTest {

    @Test
    fun `when invoked with a movie id, invoke delegates that id to repository`() = runTest {
        val repository = FakeMovieRepository(movieDetail = movie(id = 8))
        val useCase = GetMovieDetailUseCaseImpl(repository)

        useCase(8)

        assertEquals(8, repository.requestedId)
        assertEquals(1, repository.getMovieDetailInvocationCount)
    }

    @Test
    fun `when repository returns a movie, invoke returns the same movie instance`() = runTest {
        val expectedMovie = movie(id = 3)
        val repository = FakeMovieRepository(movieDetail = expectedMovie)
        val useCase = GetMovieDetailUseCaseImpl(repository)

        val result = useCase(3)

        assertEquals(1, repository.getMovieDetailInvocationCount)
        assertSame(expectedMovie, result)
    }

    @Test
    fun `when repository returns null, invoke returns null`() = runTest {
        val repository = FakeMovieRepository(movieDetail = null)
        val useCase = GetMovieDetailUseCaseImpl(repository)

        val result = useCase(42)

        assertEquals(1, repository.getMovieDetailInvocationCount)
        assertNull(result)
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
