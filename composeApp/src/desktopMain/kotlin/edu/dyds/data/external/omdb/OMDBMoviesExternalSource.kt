package edu.dyds.data.external.omdb

import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val OMDB_API_BASE_URL = "https://www.omdbapi.com/"
private const val OMDB_NA_VALUE = "N/A"
private const val OMDB_PARAM_API_KEY = "apikey"
private const val OMDB_PARAM_TITLE = "t"
private const val OMDB_PARAM_TYPE = "type"
private const val OMDB_PARAM_PLOT = "plot"
private const val OMDB_PARAM_TYPE_MOVIE = "movie"
private const val OMDB_PARAM_PLOT_FULL = "full"

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

/**
 * Converts OMDB DTO to domain Movie entity.
 * Handles special values like "N/A" and converts rating string to double.
 */
fun OMDBMovie.toDomainMovie(): Movie {
	val rating = parseImdbRating(imdbRating)
	return Movie(
		id = imdbId.hashCode(),
		title = title,
		poster = parseOmdbValue(poster),
		overview = "OMDB: " + parseOmdbValue(plot),
		popularity = rating,
		voteAverage = rating,
		releaseDate = year,
	)
}

private fun parseOmdbValue(value: String): String =
	if (value == OMDB_NA_VALUE) "" else value

private fun parseImdbRating(rating: String): Double =
	rating.toDoubleOrNull() ?: 0.0

@Suppress("unused")
class OMDBMoviesExternalSource(
	private val httpClient: HttpClient,
	private val apiKey: String,
) : edu.dyds.data.external.MovieDetailsExternalSource {

	/**
	 * Searches for a movie by title via OMDB API.
	 * Returns null if the title is not found or the request fails.
	 */
	override suspend fun searchMovieByTitle(title: String): Movie? {
		// Use 't' parameter for exact title match (returns full movie details including Plot and Rating)
		// Avoids 's' parameter which returns limited search results
		return runCatching {
			val omdb: OMDBMovie = httpClient.get(OMDB_API_BASE_URL) {
				url {
					parameters.append(OMDB_PARAM_API_KEY, apiKey)
					parameters.append(OMDB_PARAM_TITLE, title)
					parameters.append(OMDB_PARAM_TYPE, OMDB_PARAM_TYPE_MOVIE)
					parameters.append(OMDB_PARAM_PLOT, OMDB_PARAM_PLOT_FULL)
				}
			}.body()

			if (omdb.title.isBlank()) return@runCatching null

			omdb.toDomainMovie()
		}.getOrNull()
	}
}
