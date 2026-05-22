package edu.dyds.data.remote.tmdb

import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieRemoteDataSourceImplTest {

    @Test
    fun `when popular movies are requested, getPopularMovies returns parsed results`() = runTest {
        var requestedPath: String? = null
        val httpClient = testHttpClient { request ->
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
                headers = jsonHeaders,
            )
        }
        val dataSource = TMDBMoviesRemoteSourceImpl(httpClient)

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
        val httpClient = testHttpClient { request ->
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
                headers = jsonHeaders,
            )
        }
        val dataSource = TMDBMoviesRemoteSourceImpl(httpClient)

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
        val httpClient = testHttpClient {
            error("network failure")
        }
        val dataSource = TMDBMoviesRemoteSourceImpl(httpClient)

        val result = dataSource.searchMovieByTitle("Movie 42")

        assertNull(result)
    }

    private fun testHttpClient(
        handler: suspend MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData,
    ): HttpClient {
        return HttpClient(MockEngine { request -> handler(request) }) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private companion object {
        val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }
}
