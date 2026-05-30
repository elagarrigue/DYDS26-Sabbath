package edu.dyds.data.external.omdb

import edu.dyds.data.external.testhelpers.AbstractExternalSourceTest
import edu.dyds.data.external.testhelpers.PosterMapping
import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MovieRemoteDataSourceImplTest : AbstractExternalSourceTest() {
    override fun createSource(httpClient: HttpClient): Any = OMDBMoviesExternalSource(httpClient, apiKey = "testkey")

    override suspend fun callSearch(source: Any, title: String): Movie? {
        val s = source as OMDBMoviesExternalSource
        return s.searchMovieByTitle(title)
    }

    override val expectedPathForSearch: String = "/"
    override val expectedQueryKey: String = "t"
    override val expectedOverviewPrefix: String = ""
    override val posterMapping: PosterMapping = PosterMapping.Passthrough

    @Test
    fun `when a title is searched and the call succeeds, OMDB search returns mapped movie`() = runTest {
        val fixture = """
            {
              "Title":"Movie 42",
              "Poster":"https://image.omdb.org/poster42.jpg",
              "Plot":"Overview 42",
              "imdbRating":"8.4",
              "Year":"2026",
              "imdbID":"tt0042"
            }
        """

        runSearchFixtureAndVerify(
            fixture,
            "Movie 42",
            expectedPosterPath = "https://image.omdb.org/poster42.jpg",
            expectedPopularity = 8.4,
            expectedVoteAverage = 8.4,
        )
    }

    @Test
    fun `when the search request returns NA poster, poster becomes empty`() = runTest {
        val fixture = """
            {
              "Title":"Movie 43",
              "Poster":"N/A",
              "Plot":"Overview 43",
              "imdbRating":"7.0",
              "Year":"2025",
              "imdbID":"tt0043"
            }
        """

        runSearchFixtureAndVerify(
            fixture,
            "Movie 43",
            expectedPosterPath = "",
            expectedPopularity = 7.0,
            expectedVoteAverage = 7.0,
        )
    }
}



