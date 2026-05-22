package edu.dyds.data.remote.tmdb

import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.data.remote.PopularMoviesRemoteSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodedPath
import kotlinx.serialization.Serializable

@Serializable
data class TMDBSearchResult(
	val results: List<TMDBMovie> = emptyList(),
)

@Suppress("unused")
class TMDBMoviesRemoteSourceImpl(
	private val httpClient: HttpClient,
) : TMDBMoviesRemoteSource, PopularMoviesRemoteSource, MovieDetailsRemoteSource {
	override suspend fun getPopularMovies(): List<TMDBMovie> {
		val response: TMDBSearchResult = httpClient.get {
			url { encodedPath = "/3/movie/popular" }
		}.body()
		return response.results
	}

	override suspend fun searchMovieByTitle(title: String): TMDBMovie? {
		return runCatching {
			httpClient.get {
				url {
					encodedPath = "/3/search/movie"
					parameters.append("query", title)
				}
			}.body<TMDBSearchResult>().results.firstOrNull()
		}.getOrNull()
	}
}


