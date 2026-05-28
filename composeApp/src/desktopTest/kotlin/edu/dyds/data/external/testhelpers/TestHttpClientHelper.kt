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

object TestHttpClientHelper {
    fun testHttpClient(
        handler: suspend MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData,
    ): HttpClient {
        return HttpClient(MockEngine { request -> handler(request) }) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
}

