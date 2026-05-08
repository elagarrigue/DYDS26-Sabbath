package edu.dyds.domain.usecases

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetMoviesUseCaseImplTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetMoviesUseCaseImpl

    @BeforeTest
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = GetMoviesUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns movies sorted by popularity descending`() = runTest {
        repository = FakeMovieRepository(
            movies = listOf(
                movie(id = 1, popularity = 10.0, voteAverage = 6.0),
                movie(id = 2, popularity = 50.0, voteAverage = 7.0),
                movie(id = 3, popularity = 30.0, voteAverage = 8.0),
            )
        )
        useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `invoke marks movie as good when vote average is greater than seven`() = runTest {
        repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 8.5))
        )
        useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertTrue(result.single().isGoodMovie)
    }

    @Test
    fun `invoke marks movie as good when vote average is exactly seven`() = runTest {
        repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 7.0))
        )
        useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertTrue(result.single().isGoodMovie)
    }

    @Test
    fun `invoke marks movie as not good when vote average is lower than seven`() = runTest {
        repository = FakeMovieRepository(
            movies = listOf(movie(id = 1, popularity = 10.0, voteAverage = 6.9))
        )
        useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertFalse(result.single().isGoodMovie)
    }

    @Test
    fun `invoke returns empty list when repository has no movies`() = runTest {
        repository = FakeMovieRepository(movies = emptyList())
        useCase = GetMoviesUseCaseImpl(repository)

        val result = useCase()

        assertTrue(result.isEmpty())
    }

    private class FakeMovieRepository(
        private val movies: List<Movie> = emptyList(),
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
