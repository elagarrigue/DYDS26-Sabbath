package edu.dyds.data.local

import edu.dyds.domain.entities.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieLocalDataSourceImplTest {

    @Test
    fun `getCachedMovies returns empty list by default`() = runTest {
        val localDataSource = MovieLocalDataSourceImpl()

        val result = localDataSource.getCachedMovies()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `saveMovies replaces the cached movies`() = runTest {
        val localDataSource = MovieLocalDataSourceImpl()
        localDataSource.saveMovies(listOf(movie(id = 1), movie(id = 2)))

        localDataSource.saveMovies(listOf(movie(id = 3)))
        val result = localDataSource.getCachedMovies()

        assertEquals(listOf(3), result.map { it.id })
    }

    @Test
    fun `getCachedMovies returns a snapshot of the cache`() = runTest {
        val localDataSource = MovieLocalDataSourceImpl()
        localDataSource.saveMovies(listOf(movie(id = 1)))

        val snapshot = localDataSource.getCachedMovies().toMutableList()
        snapshot.add(movie(id = 2))
        val result = localDataSource.getCachedMovies()

        assertEquals(listOf(1), result.map { it.id })
    }

    @Test
    fun `getCachedMovieDetail returns movie when id exists`() = runTest {
        val expectedMovie = movie(id = 7)
        val localDataSource = MovieLocalDataSourceImpl()
        localDataSource.saveMovies(listOf(movie(id = 1), expectedMovie))

        val result = localDataSource.getCachedMovieDetail(7)

        assertEquals(expectedMovie, result)
    }

    @Test
    fun `getCachedMovieDetail returns null when id does not exist`() = runTest {
        val localDataSource = MovieLocalDataSourceImpl()
        localDataSource.saveMovies(listOf(movie(id = 1)))

        val result = localDataSource.getCachedMovieDetail(99)

        assertNull(result)
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
