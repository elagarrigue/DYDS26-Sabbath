package edu.dyds.data.external.testhelpers

import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlin.test.assertEquals
import kotlin.test.assertTrue

sealed class PosterMapping {
    data class Prefix(val base: String) : PosterMapping()
    object Passthrough : PosterMapping()
}

abstract class AbstractExternalSourceTest {
    protected abstract fun createSource(httpClient: HttpClient): Any
    protected abstract suspend fun callSearch(source: Any, title: String): Movie?

    protected open val expectedPathForSearch: String? = null
    protected open val expectedQueryKey: String? = null
    protected open val expectedOverviewPrefix: String = ""
    protected open val posterMapping: PosterMapping = PosterMapping.Passthrough

    protected suspend fun runSearchFixtureAndVerify(
        jsonFixture: String,
        queryValue: String,
        expectedPosterPath: String? = null,
        expectedPopularity: Double? = null,
        expectedVoteAverage: Double? = null,
    ) {
        var requestedPath: String? = null
        var requestedQuery: String? = null

        val httpClient = TestHttpClientHelper.testHttpClient { request ->
            requestedPath = request.url.encodedPath
            val key = expectedQueryKey
            requestedQuery = key?.let { request.url.parameters[it] }
            respond(
                content = jsonFixture.trimIndent(),
                status = HttpStatusCode.OK,
                headers = TestHttpClientHelper.jsonHeaders,
            )
        }

        val source = createSource(httpClient)
        val result = callSearch(source, queryValue)

        expectedPathForSearch?.let { assertEquals(it, requestedPath) }
        expectedQueryKey?.let { assertEquals(queryValue, requestedQuery) }

        if (result != null) {
            if (expectedOverviewPrefix.isNotEmpty()) {
                assertTrue(
                    result.overview.startsWith(expectedOverviewPrefix),
                    "overview should start with $expectedOverviewPrefix but was '${result.overview}'",
                )
            }

            expectedPosterPath?.let { posterPath ->
                val expectedPoster = when (posterMapping) {
                    is PosterMapping.Prefix -> (posterMapping as PosterMapping.Prefix).base.trimEnd('/') + "/" + posterPath.trimStart('/')
                    PosterMapping.Passthrough -> posterPath
                }
                assertEquals(expectedPoster, result.poster)
            }

            expectedPopularity?.let { assertEquals(it, result.popularity) }
            expectedVoteAverage?.let { assertEquals(it, result.voteAverage) }
        }
    }
}


