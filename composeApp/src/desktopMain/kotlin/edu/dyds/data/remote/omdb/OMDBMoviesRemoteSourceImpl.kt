package edu.dyds.data.remote.omdb

import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.data.external.omdb.OMDBMoviesExternalSourceImpl
import edu.dyds.domain.entities.Movie
import io.ktor.client.HttpClient

@Suppress("unused")
class OMDBMoviesRemoteSourceImpl(
    httpClient: HttpClient,
    apiKey: String,
) : MovieDetailsRemoteSource {
    private val externalSource = OMDBMoviesExternalSourceImpl(httpClient, apiKey)

    override suspend fun searchMovieByTitle(title: String): Movie? {
        return externalSource.searchMovieByTitle(title)
    }
}

