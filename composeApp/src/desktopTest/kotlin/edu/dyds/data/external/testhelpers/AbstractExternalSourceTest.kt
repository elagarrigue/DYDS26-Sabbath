package edu.dyds.data.external.testhelpers

import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Defines how poster URLs should be mapped in assertions. */
sealed class PosterMapping {
    /** Prefix mode: concatenate base URL with API poster path. */
    data class Prefix(val base: String) : PosterMapping()

    /** Passthrough mode: use poster URL as-is from API response. */
    object Passthrough : PosterMapping()
}

/**
 * Base test class for external movie data source implementations.
 * Provides common test infrastructure and parameterizable assertions
 * to reduce duplication when testing TMDB, OMDB, or other providers.
 *
 * Subclasses must implement [createSource] and [callSearch] to adapt
 * the test harness to their specific source implementation.
 */
abstract class AbstractExternalSourceTest {
    /**
     * Factory method to create the source under test with the given MockEngine-backed HttpClient.
     * Subclasses must instantiate their concrete source implementation here.
     */
    protected abstract fun createSource(httpClient: HttpClient): Any

    /**
     * Calls the search method on the source under test.
     * Subclasses must invoke searchMovieByTitle (or equivalent) here.
     */
    protected abstract suspend fun callSearch(source: Any, title: String): Movie?

    /** Expected API endpoint path for search requests (e.g. "/3/search/movie" for TMDB, "/" for OMDB). */
    protected open val expectedPathForSearch: String? = null

    /** Expected query parameter name used for search (e.g. "query" for TMDB, "t" for OMDB). */
    protected open val expectedQueryKey: String? = null

    /** Expected prefix in the overview field indicating the source (e.g. "TMDB: ", "OMDB: "). */
    protected open val expectedOverviewPrefix: String = ""

    /** How poster URLs are mapped from the API response. */
    protected open val posterMapping: PosterMapping = PosterMapping.Passthrough

    /**
     * Runs a search test with a JSON fixture, verifying the HTTP request and response mapping.
     *
     * @param jsonFixture JSON response body as a String.
     * @param queryValue The title/search term used in the test.
     * @param expectedPosterPath The expected poster URL in the result (after mapping).
     * @param expectedPopularity The expected popularity value.
     * @param expectedVoteAverage The expected vote average value.
     */
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

        // Verify HTTP request properties
        expectedPathForSearch?.let { assertEquals(it, requestedPath, "API endpoint path mismatch") }
        expectedQueryKey?.let { assertEquals(queryValue, requestedQuery, "Query parameter value mismatch") }

        // Verify response mapping
        if (result != null) {
            verifyOverviewPrefix(result)
            verifyPosterMapping(result, expectedPosterPath)
            verifyNumericFields(result, expectedPopularity, expectedVoteAverage)
        }
    }

    private fun verifyOverviewPrefix(result: Movie) {
        if (expectedOverviewPrefix.isNotEmpty()) {
            assertTrue(
                result.overview.startsWith(expectedOverviewPrefix),
                "Overview should start with '$expectedOverviewPrefix' but was '${result.overview}'",
            )
        }
    }

    private fun verifyPosterMapping(result: Movie, expectedPosterPath: String?) {
        expectedPosterPath?.let { posterPath ->
            val expectedPoster = when (posterMapping) {
                is PosterMapping.Prefix -> buildPrefixedUrl(posterMapping as PosterMapping.Prefix, posterPath)
                PosterMapping.Passthrough -> posterPath
            }
            assertEquals(expectedPoster, result.poster, "Poster URL mismatch")
        }
    }

    private fun buildPrefixedUrl(prefix: PosterMapping.Prefix, posterPath: String): String =
        prefix.base.trimEnd('/') + "/" + posterPath.trimStart('/')

    private fun verifyNumericFields(
        result: Movie,
        expectedPopularity: Double?,
        expectedVoteAverage: Double?,
    ) {
        expectedPopularity?.let { assertEquals(it, result.popularity, "Popularity mismatch") }
        expectedVoteAverage?.let { assertEquals(it, result.voteAverage, "Vote average mismatch") }
    }
}




