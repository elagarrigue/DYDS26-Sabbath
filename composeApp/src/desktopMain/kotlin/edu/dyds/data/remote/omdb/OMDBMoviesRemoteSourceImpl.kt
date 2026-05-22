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
        return runCatching {
            val response: OMDBSearchResult = httpClient.get("https://www.omdbapi.com/") {
                url {
                    parameters.append("apikey", apiKey)
                    parameters.append("s", title)
                    parameters.append("type", "movie")
                }
            }.body()

            // Verificar que la respuesta fue exitosa y se encontraron resultados
            response.takeIf { it.response == "True" && it.search.isNotEmpty() }
                ?.search?.firstOrNull()?.toDomainMovie()
        }.getOrNull()
    }
}

