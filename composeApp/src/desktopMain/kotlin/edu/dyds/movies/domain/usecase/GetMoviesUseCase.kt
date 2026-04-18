package edu.dyds.movies.domain.usecase

import edu.dyds.movies.QualifiedMovie
import edu.dyds.movies.RemoteMovie
import edu.dyds.movies.RemoteResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

private const val MIN_VOTE_AVERAGE = 6.0

class GetMoviesUseCase(
    private val tmdbHttpClient: HttpClient,
) {
    private val cacheMovies: MutableList<RemoteMovie> = mutableListOf()

    suspend fun execute(): List<QualifiedMovie> {
        return getPopularMovies().sortAndMap()
    }

    private suspend fun getPopularMovies(): List<RemoteMovie> =
        if (cacheMovies.isNotEmpty()) {
            cacheMovies
        } else {
            try {
                getTMDBPopularMovies().results.apply {
                    cacheMovies.clear()
                    cacheMovies.addAll(this)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

    private suspend fun getTMDBPopularMovies(): RemoteResult =
        tmdbHttpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    private fun List<RemoteMovie>.sortAndMap(): List<QualifiedMovie> {
        return this
            .sortedByDescending { it.voteAverage }
            .map {
                QualifiedMovie(
                    movie = it.toDomainMovie(),
                    isGoodMovie = it.voteAverage >= MIN_VOTE_AVERAGE
                )
            }
    }
}

