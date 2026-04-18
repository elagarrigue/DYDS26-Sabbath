package edu.dyds.movies.domain.usecase

import edu.dyds.movies.Movie
import edu.dyds.movies.RemoteMovie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class GetMovieDetailUseCase(
    private val tmdbHttpClient: HttpClient,
) {
    suspend fun execute(id: Int): Movie? {
        return getMovieDetails(id)?.toDomainMovie()
    }

    private suspend fun getMovieDetails(id: Int): RemoteMovie? =
        try {
            getTMDBMovieDetails(id)
        } catch (e: Exception) {
            null
        }

    private suspend fun getTMDBMovieDetails(id: Int): RemoteMovie =
        tmdbHttpClient.get("/3/movie/$id").body()
}

