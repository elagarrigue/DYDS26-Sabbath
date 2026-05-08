package edu.dyds.domain.usecases

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class GetMovieDetailUseCaseImplTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetMovieDetailUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = GetMovieDetailUseCaseImpl(repository)
    }

    @Test
    fun `invoke delegates requested id to repository`() = runTest {
        repository = FakeMovieRepository(movieDetail = movie(id = 8))
        useCase = GetMovieDetailUseCaseImpl(repository)

        useCase(8)

        assertEquals(8, repository.requestedId)
    }

    @Test
    fun `invoke returns movie provided by repository`() = runTest {
        val expectedMovie = movie(id = 3)
        repository = FakeMovieRepository(movieDetail = expectedMovie)
        useCase = GetMovieDetailUseCaseImpl(repository)

        val result = useCase(3)

        assertSame(expectedMovie, result)
    }

    @Test
    fun `invoke returns null when repository has no detail`() = runTest {
        repository = FakeMovieRepository(movieDetail = null)
        useCase = GetMovieDetailUseCaseImpl(repository)

        val result = useCase(42)

        assertNull(result)
    }

    private class FakeMovieRepository(
        private val movieDetail: Movie? = null,
    ) : MovieRepository {

        var requestedId: Int? = null

        override suspend fun getMovies(): List<Movie> = emptyList()

        override suspend fun getMovieDetail(id: Int): Movie? {
            requestedId = id
            return movieDetail
        }
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
