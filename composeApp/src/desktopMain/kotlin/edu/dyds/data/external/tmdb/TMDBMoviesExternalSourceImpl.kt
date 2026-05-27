package edu.dyds.data.external.tmdb

import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodedPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDBSearchResult(
	val results: List<TMDBMovie> = emptyList(),
)

@Serializable
data class TMDBMovie(
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

fun TMDBMovie.toDomainMovie(): Movie {
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

@Suppress("unused")
class TMDBMoviesExternalSourceImpl(
	private val httpClient: HttpClient,
) : TMDBMoviesExternalSource {
	override suspend fun getPopularMovies(): List<Movie> {
		val response: TMDBSearchResult = httpClient.get {
			url { encodedPath = "/3/movie/popular" }
		}.body()
		return response.results.map { it.toDomainMovie() }
	}

	override suspend fun searchMovieByTitle(title: String): Movie? {
		return runCatching {
			val tmdb = httpClient.get {
				url {
					encodedPath = "/3/search/movie"
					parameters.append("query", title)
				}
			}.body<TMDBSearchResult>().results.firstOrNull()?.toDomainMovie()
			if (tmdb == null) return@runCatching null
			// Prefix overview to indicate source when queried by title
			Movie(
				id = tmdb.id,
				title = tmdb.title,
				poster = tmdb.poster,
				backdrop = tmdb.backdrop,
				overview = if (tmdb.overview.isBlank()) tmdb.overview else "TMDB: ${tmdb.overview}",
				originalLanguage = tmdb.originalLanguage,
				originalTitle = tmdb.originalTitle,
				popularity = tmdb.popularity,
				releaseDate = tmdb.releaseDate,
				voteAverage = tmdb.voteAverage,
			)
		}.getOrNull()
	}
}

