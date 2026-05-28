package edu.dyds.data.external.testhelpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Provides reusable test utilities for mocking HTTP clients in tests.
 * Centralizes MockEngine and JSON configuration to reduce duplication across test implementations.
 */
object TestHttpClientHelper {
    /**
     * Creates an HttpClient with a MockEngine for testing.
     * Automatically configures JSON serialization with ignoreUnknownKeys to handle
     * API responses that may have extra fields not defined in the DTO.
     *
     * @param handler The lambda that handles mock requests and returns mock responses.
     * @return An HttpClient configured with MockEngine and ready for testing.
     */
    fun testHttpClient(
        handler: suspend MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData,
    ): HttpClient {
        return HttpClient(MockEngine { request -> handler(request) }) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    /** Standard JSON response headers for mock responses. */
    val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
}



