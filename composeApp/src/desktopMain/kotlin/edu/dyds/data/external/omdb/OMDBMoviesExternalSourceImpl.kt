package edu.dyds.data.external.omdb

import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodedPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OMDBMovie(
    @SerialName("imdbID")
    val imdbId: String,
    @SerialName("Title")
    val title: String,
    @SerialName("Poster")
    val poster: String,
    @SerialName("Plot")
    val plot: String,
    @SerialName("imdbRating")
    val imdbRating: String,
    @SerialName("Year")
    val year: String = "",
    @SerialName("Runtime")
    val runtime: String = "",
    @SerialName("Type")
    val type: String = "",
)

fun OMDBMovie.toDomainMovie(): Movie {
    return Movie(
        id = imdbId.hashCode(),
        title = title,
        poster = if (poster != "N/A") poster else "",
        overview = plot.takeIf { it != "N/A" } ?: "",
        popularity = try {
            imdbRating.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        },
        voteAverage = try {
            imdbRating.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        },
        releaseDate = year,
    )
}

@Suppress("unused")
class OMDBMoviesExternalSourceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
) : OMDBMoviesExternalSource {

    override suspend fun searchMovieByTitle(title: String): Movie? {
        // Use the 't' parameter to request full movie details by title (returns a single movie)
        // The 's' (search) endpoint returns limited fields (no Plot/imdbRating), so switch to 't'.
        return runCatching {
            val omdb: OMDBMovie = httpClient.get("https://www.omdbapi.com/") {
                url {
                    parameters.append("apikey", apiKey)
                    parameters.append("t", title)
                    parameters.append("type", "movie")
                    parameters.append("plot", "full")
                }
            }.body()

            if (omdb.title.isBlank()) return@runCatching null

            val domain = omdb.toDomainMovie()
            Movie(
                id = domain.id,
                title = domain.title,
                poster = domain.poster,
                backdrop = domain.backdrop,
                overview = if (domain.overview.isBlank()) domain.overview else "OMDB: ${domain.overview}",
                originalLanguage = domain.originalLanguage,
                originalTitle = domain.originalTitle,
                popularity = domain.popularity,
                releaseDate = domain.releaseDate,
                voteAverage = domain.voteAverage,
            )
        }.getOrNull()
    }
}

