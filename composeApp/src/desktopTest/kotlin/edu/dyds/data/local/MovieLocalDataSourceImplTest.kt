package edu.dyds.data.local

import edu.dyds.domain.entities.Movie
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class MovieLocalDataSourceImplTest {

    @Test
    fun `when cache was never populated, getCachedMovies returns an empty list`() = runTest {
        val localDataSource: MovieLocalDataSource = MovieLocalDataSourceImpl()

        val result = localDataSource.getCachedMovies()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `when saveMovies is called twice, the second call replaces the cache`() = runTest {
        val localDataSource: MovieLocalDataSource = MovieLocalDataSourceImpl()

        localDataSource.saveMovies(listOf(movie(id = 1), movie(id = 2)))

        localDataSource.saveMovies(listOf(movie(id = 3)))
        val result = localDataSource.getCachedMovies()

        assertEquals(listOf(3), result.map { it.id })
    }

    @Test
    fun `when cached movies are read, getCachedMovies returns a snapshot of the cache`() = runTest {
        val localDataSource: MovieLocalDataSource = MovieLocalDataSourceImpl()

        localDataSource.saveMovies(listOf(movie(id = 1)))

        val snapshot = localDataSource.getCachedMovies().toMutableList()
        snapshot.add(movie(id = 2))
        val result = localDataSource.getCachedMovies()

        assertEquals(listOf(1), result.map { it.id })
    }

    @Test
    fun `when requested id exists in cache, getCachedMovieDetail returns that movie`() = runTest {
        val localDataSource: MovieLocalDataSource = MovieLocalDataSourceImpl()
        val expectedMovie = movie(id = 7)
        localDataSource.saveMovies(listOf(movie(id = 1), expectedMovie))

        val result = localDataSource.getCachedMovieDetail(7)

        assertEquals(expectedMovie, result)
    }

    @Test
    fun `when requested id does not exist in cache, getCachedMovieDetail returns null`() = runTest {
        val localDataSource: MovieLocalDataSource = MovieLocalDataSourceImpl()
        localDataSource.saveMovies(listOf(movie(id = 1)))

        val result = localDataSource.getCachedMovieDetail(99)

        assertNull(result)
    }

    @Test
    fun `when reads and writes happen concurrently, cache operations remain thread safe`() {
        val localDataSource: MovieLocalDataSource = MovieLocalDataSourceImpl()
        val movieCount = 50
        val testMovies = (1..movieCount).map { movie(id = it) }

        val threads = mutableListOf<Thread>()
        val errors = mutableListOf<Throwable>()

        // Launch write threads
        repeat(5) {
            threads.add(Thread {
                try {
                    repeat(10) {
                        runBlocking {
                            localDataSource.saveMovies(testMovies)
                        }
                    }
                } catch (e: Exception) {
                    errors.add(e)
                }
            })
        }

        // Launch read threads
        repeat(5) {
            threads.add(Thread {
                try {
                    repeat(20) {
                        runBlocking {
                            localDataSource.getCachedMovies()
                        }
                    }
                } catch (e: Exception) {
                    errors.add(e)
                }
            })
        }

        // Launch detail lookup threads
        repeat(5) {
            threads.add(Thread {
                try {
                    repeat(20) {
                        for (id in 1..movieCount) {
                            runBlocking {
                                localDataSource.getCachedMovieDetail(id)
                            }
                        }
                    }
                } catch (e: Exception) {
                    errors.add(e)
                }
            })
        }

        // Start all threads
        threads.forEach { it.start() }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Verify no errors occurred
        assertEquals(emptyList(), errors)

        // Verify final state is consistent
        val finalMovies = runBlocking { localDataSource.getCachedMovies() }
        assertEquals(movieCount, finalMovies.size)
        assertEquals((1..movieCount).toList(), finalMovies.map { it.id })
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
