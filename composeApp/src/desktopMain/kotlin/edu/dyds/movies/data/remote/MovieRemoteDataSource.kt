package edu.dyds.movies.data.remote

import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodedPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MovieRemoteDataSource(
	private val httpClient: HttpClient,
) {
	suspend fun getPopularMovies(): List<RemoteMovie> {
		val response: RemoteResult = httpClient.get {
			url { encodedPath = "/3/movie/popular" }
		}.body()
		return response.results
	}

	suspend fun getMovieDetail(id: Int): RemoteMovie? {
		return runCatching {
			httpClient.get {
				url { encodedPath = "/3/movie/$id" }
			}.body<RemoteMovie>()
		}.getOrNull()
	}
}

@Serializable
data class RemoteResult(
	val results: List<RemoteMovie> = emptyList(),
)

@Serializable
data class RemoteMovie(
	val id: Int,
	val title: String = "",
	@SerialName("poster_path") val posterPath: String? = null,
	@SerialName("backdrop_path") val backdropPath: String? = null,
	val overview: String = "",
	@SerialName("original_language") val originalLanguage: String = "",
	@SerialName("original_title") val originalTitle: String = "",
	val popularity: Double = 0.0,
	@SerialName("release_date") val releaseDate: String = "",
	@SerialName("vote_average") val voteAverage: Double = 0.0,
)

fun RemoteMovie.toDomainMovie(): Movie {
	return Movie(
		id = id,
		title = title,
		poster = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
		backdrop = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
		overview = overview,
		originalLanguage = originalLanguage,
		originalTitle = originalTitle,
		popularity = popularity,
		releaseDate = releaseDate,
		voteAverage = voteAverage,
	)
}



