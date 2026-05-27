package edu.dyds.data.repositoriesImpl

import edu.dyds.data.fakes.FakeMovieLocalDataSource
import edu.dyds.data.fakes.FakePopularMoviesRemoteSource
import edu.dyds.data.fakes.FakeMovieDetailsRemoteSource
import edu.dyds.data.external.tmdb.TMDBMovie
import edu.dyds.data.external.tmdb.toDomainMovie
import edu.dyds.domain.entities.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieRepositoryImplTest {

    @Test
    fun `when local cache already contains movies, getMovies returns cached movies`() = runTest {
        val cachedMovies = listOf(movie(id = 1), movie(id = 2))
        val popularMoviesSource = FakePopularMoviesRemoteSource(popularMovies = listOf(remoteMovie(id = 99)))
        val detailsSource = FakeMovieDetailsRemoteSource()
        val localDataSource = FakeMovieLocalDataSource(cachedMovies = cachedMovies)
        val repository = MovieRepositoryImpl(popularMoviesSource, detailsSource, localDataSource)

        val result = repository.getMovies()

        assertEquals(cachedMovies, result)
        assertEquals(0, popularMoviesSource.getPopularMoviesInvocationCount)
        assertEquals(0, localDataSource.saveInvocationCount)
    }

    @Test
    fun `when local cache is empty, getMovies fetches maps and stores remote movies`() = runTest {
        val popularMoviesSource = FakePopularMoviesRemoteSource(
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
        val detailsSource = FakeMovieDetailsRemoteSource()
        val localDataSource = FakeMovieLocalDataSource()
        val repository = MovieRepositoryImpl(popularMoviesSource, detailsSource, localDataSource)

        val result = repository.getMovies()

        assertEquals(1, popularMoviesSource.getPopularMoviesInvocationCount)
        assertEquals(1, localDataSource.saveInvocationCount)
        assertEquals(result, localDataSource.savedMovies)
        assertEquals(1, result.size)
        assertEquals(1, result[0].id)
        assertEquals("Movie 1", result[0].title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster1.jpg", result[0].poster)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop1.jpg", result[0].backdrop)
        assertEquals("TMDB: Overview 1", result[0].overview)
        assertEquals("en", result[0].originalLanguage)
        assertEquals("Original 1", result[0].originalTitle)
        assertEquals(100.5, result[0].popularity)
        assertEquals("2026-01-01", result[0].releaseDate)
        assertEquals(8.1, result[0].voteAverage)
    }

    @Test
    fun `when requested detail is already cached, getMovieDetailByTitle still prefers remote detail`() = runTest {
        val cachedMovie = movie(id = 7, title = "Movie 7")
        val popularMoviesSource = FakePopularMoviesRemoteSource()
        val detailsSource = FakeMovieDetailsRemoteSource(
            movieDetail = Movie(
                id = 99,
                title = "Movie 7",
                poster = "poster-99",
                overview = "TMDB: Overview 99",
            )
        )
        val localDataSource = FakeMovieLocalDataSource(cachedMovies = listOf(cachedMovie))
        val repository = MovieRepositoryImpl(popularMoviesSource, detailsSource, localDataSource)

        val result = repository.getMovieDetailByTitle("Movie 7")

        assertEquals(1, detailsSource.searchMovieByTitleInvocationCount)
        assertEquals("Movie 7", detailsSource.requestedTitle)
        assertEquals(99, result?.id)
        assertEquals("Movie 7", result?.title)
        assertEquals("TMDB: Overview 99", result?.overview)
        assertEquals(1, localDataSource.saveInvocationCount)
    }

    @Test
    fun `when detail is missing from cache and remote has it, getMovieDetailByTitle keeps cache and returns mapped movie`() = runTest {
        val detailsSource = FakeMovieDetailsRemoteSource(
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
        val popularMoviesSource = FakePopularMoviesRemoteSource()
        val localDataSource = FakeMovieLocalDataSource(cachedMovies = listOf(movie(id = 8)))
        val repository = MovieRepositoryImpl(popularMoviesSource, detailsSource, localDataSource)

        val result = repository.getMovieDetailByTitle("Movie 7")

        assertEquals(1, detailsSource.searchMovieByTitleInvocationCount)
        assertEquals("Movie 7", detailsSource.requestedTitle)
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
    fun `when cache already has other movies, getMovieDetailByTitle appends fetched detail to cache`() = runTest {
        val detailsSource = FakeMovieDetailsRemoteSource(
            movieDetail = remoteMovie(id = 5, title = "Movie 5")
        )
        val popularMoviesSource = FakePopularMoviesRemoteSource()
        val localDataSource = FakeMovieLocalDataSource(cachedMovies = listOf(movie(id = 1)))
        val repository = MovieRepositoryImpl(popularMoviesSource, detailsSource, localDataSource)

        val result = repository.getMovieDetailByTitle("Movie 5")

        assertEquals(1, detailsSource.searchMovieByTitleInvocationCount)
        assertEquals("Movie 5", detailsSource.requestedTitle)
        assertEquals(1, localDataSource.saveInvocationCount)
        assertEquals(listOf(1, 5), localDataSource.savedMovies?.map { it.id })
        assertEquals(5, result?.id)
    }

    @Test
    fun `when remote data source returns no detail, getMovieDetailByTitle returns null without updating cache`() = runTest {
        val detailsSource = FakeMovieDetailsRemoteSource(movieDetail = null)
        val popularMoviesSource = FakePopularMoviesRemoteSource()
        val localDataSource = FakeMovieLocalDataSource()
        val repository = MovieRepositoryImpl(popularMoviesSource, detailsSource, localDataSource)

        val result = repository.getMovieDetailByTitle("Movie 99")

        assertEquals(1, detailsSource.searchMovieByTitleInvocationCount)
        assertEquals("Movie 99", detailsSource.requestedTitle)
        assertNull(result)
        assertEquals(0, localDataSource.saveInvocationCount)
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
    ) = TMDBMovie(
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
    ).toDomainMovie()

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
