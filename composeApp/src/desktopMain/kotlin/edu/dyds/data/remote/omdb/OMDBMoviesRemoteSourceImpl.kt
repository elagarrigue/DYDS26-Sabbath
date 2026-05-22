package edu.dyds.data.remote.omdb

import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodedPath

@Suppress("unused")
class OMDBMoviesRemoteSourceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
) : MovieDetailsRemoteSource {

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

