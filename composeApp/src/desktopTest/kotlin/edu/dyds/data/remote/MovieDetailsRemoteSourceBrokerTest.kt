package edu.dyds.data.remote

import edu.dyds.domain.entities.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MovieDetailsRemoteSourceBrokerTest {

    @Test
    fun `when both sources return a movie, broker combines overview popularity and vote average`() = runTest {
        val tmdbSource = FakeDetailsSource(
            movie = movie(
                id = 1,
                title = "Movie 1",
                overview = "TMDB overview",
                popularity = 80.0,
                voteAverage = 7.0,
            )
        )
        val omdbSource = FakeDetailsSource(
            movie = movie(
                id = 2,
                title = "Movie 1",
                overview = "OMDB overview",
                popularity = 40.0,
                voteAverage = 9.0,
            )
        )
        val broker = MovieDetailsRemoteSourceBroker(tmdbSource, omdbSource)

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
        val tmdbSource = FakeDetailsSource(movie = movie(id = 1, title = "Movie 1", overview = "TMDB overview"))
        val omdbSource = FakeDetailsSource(movie = null)
        val broker = MovieDetailsRemoteSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("TMDB: TMDB overview", result?.overview)
    }

    @Test
    fun `when only OMDB returns a movie, broker prefixes overview with OMDB`() = runTest {
        val tmdbSource = FakeDetailsSource(movie = null)
        val omdbSource = FakeDetailsSource(movie = movie(id = 2, title = "Movie 1", overview = "OMDB overview"))
        val broker = MovieDetailsRemoteSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("OMDB: OMDB overview", result?.overview)
    }

    @Test
    fun `when neither source returns a movie, broker returns an empty movie`() = runTest {
        val tmdbSource = FakeDetailsSource(movie = null)
        val omdbSource = FakeDetailsSource(movie = null)
        val broker = MovieDetailsRemoteSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals(0, result?.id)
        assertEquals("", result?.title)
        assertEquals("", result?.poster)
        assertEquals("", result?.overview)
    }

    @Test
    fun `when TMDB fails but OMDB returns a movie, broker falls back to OMDB`() = runTest {
        val tmdbSource = ThrowingDetailsSource()
        val omdbSource = FakeDetailsSource(movie = movie(id = 2, title = "Movie 1", overview = "OMDB overview"))
        val broker = MovieDetailsRemoteSourceBroker(tmdbSource, omdbSource)

        val result = broker.searchMovieByTitle("Movie 1")

        assertEquals(1, tmdbSource.invocationCount)
        assertEquals(1, omdbSource.invocationCount)
        assertEquals("OMDB: OMDB overview", result?.overview)
    }

    @Test
    fun `when OMDB fails but TMDB returns a movie, broker falls back to TMDB`() = runTest {
        val tmdbSource = FakeDetailsSource(movie = movie(id = 1, title = "Movie 1", overview = "TMDB overview"))
        val omdbSource = ThrowingDetailsSource()
        val broker = MovieDetailsRemoteSourceBroker(tmdbSource, omdbSource)

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

    private class FakeDetailsSource(
        private val movie: Movie?,
    ) : MovieDetailsRemoteSource {
        var invocationCount: Int = 0
        var requestedTitle: String? = null

        override suspend fun searchMovieByTitle(title: String): Movie? {
            invocationCount++
            requestedTitle = title
            return movie
        }
    }

    private class ThrowingDetailsSource : MovieDetailsRemoteSource {
        var invocationCount: Int = 0

        override suspend fun searchMovieByTitle(title: String): Movie? {
            invocationCount++
            error("boom: $title")
        }
    }
}

