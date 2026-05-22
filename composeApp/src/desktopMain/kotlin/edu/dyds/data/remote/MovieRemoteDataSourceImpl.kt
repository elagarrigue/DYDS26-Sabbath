package edu.dyds.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodedPath
import kotlinx.serialization.Serializable

@Serializable
data class RemoteResult(
    val results: List<RemoteMovie> = emptyList(),
)

@Suppress("unused")
class MovieRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : MovieRemoteDataSource {
    override suspend fun getPopularMovies(): List<RemoteMovie> {
        val response: RemoteResult = httpClient.get {
            url { encodedPath = "/3/movie/popular" }
        }.body()
        return response.results
    }

    override suspend fun searchMovieByTitle(title: String): RemoteMovie? {
        return runCatching {
            httpClient.get {
                url {
                    encodedPath = "/3/search/movie"
                    parameters.append("query", title)
                }
            }.body<RemoteResult>().results.firstOrNull()
        }.getOrNull()
    }
}
