package edu.dyds.data.repositoriesImpl

import edu.dyds.data.local.MovieLocalDataSource
import edu.dyds.data.remote.MovieRemoteDataSource
import edu.dyds.data.remote.RemoteMovie
import edu.dyds.domain.entities.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieRepositoryImplTest {

    private lateinit var remoteDataSource: FakeMovieRemoteDataSource
    private lateinit var localDataSource: FakeMovieLocalDataSource
    private lateinit var repository: MovieRepositoryImpl

    @BeforeTest
    fun setUp() {
        remoteDataSource = FakeMovieRemoteDataSource()
        localDataSource = FakeMovieLocalDataSource()
        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getMovies returns cached movies when local data source is not empty`() = runTest {
        val cachedMovies = listOf(movie(id = 1), movie(id = 2))
        remoteDataSource = FakeMovieRemoteDataSource(popularMovies = listOf(remoteMovie(id = 99)))
        localDataSource = FakeMovieLocalDataSource(cachedMovies = cachedMovies)
        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)

        val result = repository.getMovies()

        assertEquals(cachedMovies, result)
        assertEquals(0, remoteDataSource.getPopularMoviesInvocationCount)
        assertEquals(0, localDataSource.saveInvocationCount)
    }

    @Test
    fun `getMovies fetches remote movies maps them and stores them when cache is empty`() = runTest {
        remoteDataSource = FakeMovieRemoteDataSource(
            popularMovies = listOf(
                remoteMovie(
                    id = 1,
                    title = "Movie 1",
                    posterPath = "/poster1.jpg",
                    backdropPath = "/backdrop1.jpg",
                    overview = "Overview 1",
                    originalLanguage = "en",
                    originalTitle = "Original 1",
                    popularity = 100.5,
                    releaseDate = "2026-01-01",
                    voteAverage = 8.1,
                )
            )
        )
        localDataSource = FakeMovieLocalDataSource()
        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)

        val result = repository.getMovies()

        assertEquals(1, remoteDataSource.getPopularMoviesInvocationCount)
        assertEquals(1, localDataSource.saveInvocationCount)
        assertEquals(result, localDataSource.savedMovies)
        assertEquals(1, result.size)
        assertEquals(1, result[0].id)
        assertEquals("Movie 1", result[0].title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster1.jpg", result[0].poster)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop1.jpg", result[0].backdrop)
        assertEquals("Overview 1", result[0].overview)
        assertEquals("en", result[0].originalLanguage)
        assertEquals("Original 1", result[0].originalTitle)
        assertEquals(100.5, result[0].popularity)
        assertEquals("2026-01-01", result[0].releaseDate)
        assertEquals(8.1, result[0].voteAverage)
    }

    @Test
    fun `getMovieDetail returns cached detail when local data source has it`() = runTest {
        val cachedMovie = movie(id = 7)
        remoteDataSource = FakeMovieRemoteDataSource(movieDetail = remoteMovie(id = 99))
        localDataSource = FakeMovieLocalDataSource(cachedMovies = listOf(cachedMovie))
        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)

        val result = repository.getMovieDetail(7)

        assertEquals(cachedMovie, result)
        assertNull(remoteDataSource.requestedId)
        assertEquals(0, localDataSource.saveInvocationCount)
    }

    @Test
    fun `getMovieDetail fetches remote detail keeps existing cache and returns mapped movie`() = runTest {
        remoteDataSource = FakeMovieRemoteDataSource(
            movieDetail = remoteMovie(
                id = 7,
                title = "Movie 7",
                posterPath = "/poster7.jpg",
                backdropPath = "/backdrop7.jpg",
                overview = "Overview 7",
                originalLanguage = "es",
                originalTitle = "Original 7",
                popularity = 70.7,
                releaseDate = "2026-07-07",
                voteAverage = 7.7,
            )
        )
        localDataSource = FakeMovieLocalDataSource(cachedMovies = listOf(movie(id = 8)))
        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)

        val result = repository.getMovieDetail(7)

        assertEquals(7, remoteDataSource.requestedId)
        assertEquals(1, localDataSource.saveInvocationCount)
        assertEquals(2, localDataSource.savedMovies?.size)
        assertEquals(listOf(8, 7), localDataSource.savedMovies?.map { it.id })
        assertEquals(7, result?.id)
        assertEquals("Movie 7", result?.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster7.jpg", result?.poster)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop7.jpg", result?.backdrop)
        assertEquals("Overview 7", result?.overview)
        assertEquals("es", result?.originalLanguage)
        assertEquals("Original 7", result?.originalTitle)
        assertEquals(70.7, result?.popularity)
        assertEquals("2026-07-07", result?.releaseDate)
        assertEquals(7.7, result?.voteAverage)
        assertEquals(result, localDataSource.savedMovies?.first { it.id == 7 })
    }

    @Test
    fun `getMovieDetail adds remote detail to cache when movie was not cached`() = runTest {
        remoteDataSource = FakeMovieRemoteDataSource(
            movieDetail = remoteMovie(id = 5, title = "Movie 5")
        )
        localDataSource = FakeMovieLocalDataSource(cachedMovies = listOf(movie(id = 1)))
        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)

        val result = repository.getMovieDetail(5)

        assertEquals(5, remoteDataSource.requestedId)
        assertEquals(1, localDataSource.saveInvocationCount)
        assertEquals(listOf(1, 5), localDataSource.savedMovies?.map { it.id })
        assertEquals(5, result?.id)
    }

    @Test
    fun `getMovieDetail returns null when remote data source has no detail`() = runTest {
        remoteDataSource = FakeMovieRemoteDataSource(movieDetail = null)
        localDataSource = FakeMovieLocalDataSource()
        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)

        val result = repository.getMovieDetail(99)

        assertEquals(99, remoteDataSource.requestedId)
        assertNull(result)
        assertEquals(0, localDataSource.saveInvocationCount)
    }

    private class FakeMovieRemoteDataSource(
        private val popularMovies: List<RemoteMovie> = emptyList(),
        private val movieDetail: RemoteMovie? = null,
    ) : MovieRemoteDataSource {

        var requestedId: Int? = null
        var getPopularMoviesInvocationCount: Int = 0

        override suspend fun getPopularMovies(): List<RemoteMovie> {
            getPopularMoviesInvocationCount++
            return popularMovies
        }

        override suspend fun getMovieDetail(id: Int): RemoteMovie? {
            requestedId = id
            return movieDetail
        }
    }

    private class FakeMovieLocalDataSource(
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

        override suspend fun getCachedMovieDetail(id: Int): Movie? {
            return cachedMoviesState.find { it.id == id }
        }
    }

    private fun remoteMovie(
        id: Int,
        title: String = "Movie $id",
        posterPath: String? = "/poster$id.jpg",
        backdropPath: String? = "/backdrop$id.jpg",
        overview: String = "Overview $id",
        originalLanguage: String = "en",
        originalTitle: String = "Original $id",
        popularity: Double = 10.0,
        releaseDate: String = "2026-01-01",
        voteAverage: Double = 7.0,
    ) = RemoteMovie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
    )

    private fun movie(
        id: Int,
        title: String = "Movie $id",
        poster: String = "poster-$id",
    ) = Movie(
        id = id,
        title = title,
        poster = poster,
    )
}
