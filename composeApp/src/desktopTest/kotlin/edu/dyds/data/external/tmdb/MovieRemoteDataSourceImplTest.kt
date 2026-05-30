package edu.dyds.data.external.tmdb

import edu.dyds.data.external.testhelpers.TestHttpClientHelper
import io.ktor.client.engine.mock.respond
import edu.dyds.data.external.tmdb.TMDBMoviesExternalSourceImpl
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieRemoteDataSourceImplTest {

    @Test
    fun `when popular movies are requested, getPopularMovies returns parsed results`() = runTest {
        var requestedPath: String? = null
        val httpClient = TestHttpClientHelper.testHttpClient { request ->
            requestedPath = request.url.encodedPath
            respond(
                content = """
                    {
                      "results": [
                        {
                          "id": 7,
                          "title": "Movie 7",
                          "poster_path": "/poster7.jpg",
                          "backdrop_path": "/backdrop7.jpg",
                          "overview": "Overview 7",
                          "original_language": "es",
                          "original_title": "Original 7",
                          "popularity": 70.7,
                          "release_date": "2026-07-07",
                          "vote_average": 7.7
                        }
                      ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = TestHttpClientHelper.jsonHeaders,
            )
        }
        val dataSource = TMDBMoviesExternalSourceImpl(httpClient)

        val result = dataSource.getPopularMovies()

        assertEquals("/3/movie/popular", requestedPath)
        assertEquals(1, result.size)
        val movie = result.single()
        assertEquals("Movie 7", movie.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster7.jpg", movie.poster)
        assertEquals("Overview 7", movie.overview)
        assertEquals(70.7, movie.popularity)
        assertEquals(7.7, movie.voteAverage)
    }

    @Test
    fun `when a title is searched and the call succeeds, searchMovieByTitle returns the first parsed movie`() = runTest {
        var requestedPath: String? = null
        var requestedQuery: String? = null
        val httpClient = TestHttpClientHelper.testHttpClient { request ->
            requestedPath = request.url.encodedPath
            requestedQuery = request.url.parameters["query"]
            respond(
                content = """
                    {
                      "results": [
                        {
                          "id": 42,
                          "title": "Movie 42",
                          "poster_path": "/poster42.jpg",
                          "backdrop_path": "/backdrop42.jpg",
                          "overview": "Overview 42",
                          "original_language": "en",
                          "original_title": "Original 42",
                          "popularity": 42.0,
                          "release_date": "2026-04-02",
                          "vote_average": 8.4
                        }
                      ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = TestHttpClientHelper.jsonHeaders,
            )
        }
        val dataSource = TMDBMoviesExternalSourceImpl(httpClient)

        val result = dataSource.searchMovieByTitle("Movie 42")

        assertEquals("/3/search/movie", requestedPath)
        assertEquals("Movie 42", requestedQuery)
        assertEquals("Movie 42", result?.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster42.jpg", result?.poster)
        assertEquals("Overview 42", result?.overview)
        assertEquals(42.0, result?.popularity)
        assertEquals(8.4, result?.voteAverage)
    }

    @Test
    fun `when the search request fails, searchMovieByTitle returns null`() = runTest {
        val httpClient = TestHttpClientHelper.testHttpClient {
            error("network failure")
        }
        val dataSource = TMDBMoviesExternalSourceImpl(httpClient)

        val result = dataSource.searchMovieByTitle("Movie 42")

        assertNull(result)
    }
}


