package edu.dyds.data.external

import edu.dyds.data.fakes.FakeMovieDetailsRemoteSource
import edu.dyds.data.fakes.FakeThrowingMovieDetailsRemoteSource
import edu.dyds.domain.entities.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieDetailsExternalSourceBrokerTest {

    @Test
    fun `when both sources return a movie, broker combines overview popularity and vote average`() = runTest {
        val tmdbSource = FakeMovieDetailsRemoteSource(
            movie = movie(
                id = 1,
                title = "Movie 1",
                overview = "TMDB overview",
                popularity = 80.0,
                voteAverage = 7.0,
            )
        )
        val omdbSource = FakeMovieDetailsRemoteSource(
            movie = movie(
                id = 2,
                title = "Movie 1",
                overview = "OMDB overview",
                popularity = 40.0,
                voteAverage = 9.0,
            )
        )
        val broker = MovieDetailsExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("Movie 1", tmdbSource.requestedTitle)
        assertEquals("Movie 1", omdbSource.requestedTitle)
        assertEquals(1, result?.id)
        assertEquals("Movie 1", result?.title)
        assertEquals("TMDB: TMDB overview\nOMDB: OMDB overview", result?.overview)
        assertEquals(60.0, result?.popularity)
        assertEquals(8.0, result?.voteAverage)
    }

    @Test
    fun `when only TMDB returns a movie, broker prefixes overview with TMDB`() = runTest {
        val tmdbSource = FakeMovieDetailsRemoteSource(movie = movie(id = 1, title = "Movie 1", overview = "TMDB overview"))
        val omdbSource = FakeMovieDetailsRemoteSource(movie = null)
        val broker = MovieDetailsExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("TMDB: TMDB overview", result?.overview)
    }

    @Test
    fun `when only OMDB returns a movie, broker prefixes overview with OMDB`() = runTest {
        val tmdbSource = FakeMovieDetailsRemoteSource(movie = null)
        val omdbSource = FakeMovieDetailsRemoteSource(movie = movie(id = 2, title = "Movie 1", overview = "OMDB overview"))
        val broker = MovieDetailsExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("OMDB: OMDB overview", result?.overview)
    }

    @Test
    fun `when neither source returns a movie, broker returns null`() = runTest {
        val tmdbSource = FakeMovieDetailsRemoteSource(movie = null)
        val omdbSource = FakeMovieDetailsRemoteSource(movie = null)
        val broker = MovieDetailsExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertNull(result)
    }

    @Test
    fun `when TMDB fails but OMDB returns a movie, broker falls back to OMDB`() = runTest {
        val tmdbSource = FakeThrowingMovieDetailsRemoteSource()
        val omdbSource = FakeMovieDetailsRemoteSource(movie = movie(id = 2, title = "Movie 1", overview = "OMDB overview"))
        val broker = MovieDetailsExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("OMDB: OMDB overview", result?.overview)
    }

    @Test
    fun `when OMDB fails but TMDB returns a movie, broker falls back to TMDB`() = runTest {
        val tmdbSource = FakeMovieDetailsRemoteSource(movie = movie(id = 1, title = "Movie 1", overview = "TMDB overview"))
        val omdbSource = FakeThrowingMovieDetailsRemoteSource()
        val broker = MovieDetailsExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("TMDB: TMDB overview", result?.overview)
    }

    private fun movie(
        id: Int,
        title: String,
        overview: String,
        popularity: Double = 50.0,
        voteAverage: Double = 7.0,
    ) = Movie(
        id = id,
        title = title,
        poster = "poster-$id",
        backdrop = "backdrop-$id",
        overview = overview,
        originalLanguage = "en",
        originalTitle = title,
        popularity = popularity,
        releaseDate = "2026-01-01",
        voteAverage = voteAverage,
    )

}


