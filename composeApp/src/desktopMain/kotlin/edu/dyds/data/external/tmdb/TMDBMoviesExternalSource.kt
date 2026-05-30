package edu.dyds.data.external.tmdb

import edu.dyds.domain.entities.Movie
import edu.dyds.data.external.PopularMoviesExternalSource
import edu.dyds.data.external.MovieDetailsExternalSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodedPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val TMDB_BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"
private const val TMDB_ENDPOINT_POPULAR = "/3/movie/popular"
private const val TMDB_ENDPOINT_SEARCH = "/3/search/movie"
private const val TMDB_PARAM_QUERY = "query"

@Serializable
data class TMDBSearchResult(
    val results: List<TMDBMovie> = emptyList(),
    @SerialName("status_code") val statusCode: Int? = null,
    @SerialName("status_message") val statusMessage: String? = null,
    val success: Boolean? = null,
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

/**
 * Converts TMDB DTO to domain Movie entity.
 * Constructs poster and backdrop URLs using TMDB's image CDN.
 */
fun TMDBMovie.toDomainMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        poster = posterPath?.let { "$TMDB_POSTER_BASE_URL$it" } ?: "",
        backdrop = backdropPath?.let { "$TMDB_BACKDROP_BASE_URL$it" },
        overview = overview,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
    )
}

/**
 * TMDB-specific implementation of external movie sources.
 * Provides both popular movies and movie details by title.
 * Directly implements both PopularMoviesExternalSource and MovieDetailsExternalSource.
 */
@Suppress("unused")
class TMDBMoviesExternalSource(
    private val httpClient: HttpClient,
) : PopularMoviesExternalSource, MovieDetailsExternalSource {

    /**
     * Fetches a list of popular movies from TMDB.
     * Throws IllegalStateException if the API response contains an error status.
     */
    override suspend fun getPopularMovies(): List<Movie> {
        val response: TMDBSearchResult = httpClient.get {
            url { encodedPath = TMDB_ENDPOINT_POPULAR }
        }.body()

        validateResponse(response)
        return response.results.map { it.toDomainMovie() }
    }

    /**
     * Searches for a movie by title via TMDB API.
     * Returns the first result if found, or null if not found or request fails.
     * Adds a source prefix to the overview for clarity.
     */
    override suspend fun searchMovieByTitle(title: String): Movie? {
        return runCatching {
            val response: TMDBSearchResult = httpClient.get {
                url {
                    encodedPath = TMDB_ENDPOINT_SEARCH
                    parameters.append(TMDB_PARAM_QUERY, title)
                }
            }.body()

            validateResponse(response)
            response.results.firstOrNull()?.toDomainMovie()
        }.getOrNull()
    }

    private fun validateResponse(response: TMDBSearchResult) {
        if (response.statusCode != null) {
            throw IllegalStateException(
                response.statusMessage ?: "TMDB request failed with status_code=${response.statusCode}"
            )
        }
    }

}

