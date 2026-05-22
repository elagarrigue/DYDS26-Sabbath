package edu.dyds.data.remote.omdb

import edu.dyds.domain.entities.Movie
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

