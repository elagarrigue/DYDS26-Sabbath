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
    fun `when invoked with a movie title, invoke delegates that title to repository`() = runTest {
        val repository = FakeMovieRepository(movieDetail = movie(id = 8))
        val useCase = GetMovieDetailUseCaseImpl(repository)

        useCase("Movie 8")

        assertEquals("Movie 8", repository.requestedTitle)
        assertEquals(1, repository.getMovieDetailByTitleInvocationCount)
    }

    @Test
    fun `when repository returns a movie, invoke returns the same movie instance`() = runTest {
        val expectedMovie = movie(id = 3)
        val repository = FakeMovieRepository(movieDetail = expectedMovie)
        val useCase = GetMovieDetailUseCaseImpl(repository)

        val result = useCase("Movie 3")

        assertEquals(1, repository.getMovieDetailByTitleInvocationCount)
        assertSame(expectedMovie, result)
    }

    @Test
    fun `when repository returns null, invoke returns null`() = runTest {
        val repository = FakeMovieRepository(movieDetail = null)
        val useCase = GetMovieDetailUseCaseImpl(repository)

        val result = useCase("Movie 42")

        assertEquals(1, repository.getMovieDetailByTitleInvocationCount)
        assertNull(result)
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
