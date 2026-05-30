package edu.dyds.data.external

import edu.dyds.domain.entities.Movie

@Suppress("unused")
class MovieDetailsExternalSourceBroker(
    private val tmdbSource: MovieDetailsExternalSource,
    private val omdbSource: MovieDetailsExternalSource,
) : MovieDetailsExternalSource {

    override suspend fun searchMovieByTitle(title: String): Movie? {
        val tmdbResult: Movie? = runCatching { tmdbSource.searchMovieByTitle(title) }.getOrNull()
        val omdbResult: Movie? = runCatching { omdbSource.searchMovieByTitle(title) }.getOrNull()

        return when {
            tmdbResult != null && omdbResult != null -> buildMovie(tmdbResult, omdbResult)
            tmdbResult != null -> Movie(
                id = tmdbResult.id,
                title = tmdbResult.title,
                poster = tmdbResult.poster,
                backdrop = tmdbResult.backdrop,
                overview = prefixOverview("TMDB", tmdbResult.overview),
                originalLanguage = tmdbResult.originalLanguage,
                originalTitle = tmdbResult.originalTitle,
                popularity = tmdbResult.popularity,
                releaseDate = tmdbResult.releaseDate,
                voteAverage = tmdbResult.voteAverage,
            )
            omdbResult != null -> Movie(
                id = omdbResult.id,
                title = omdbResult.title,
                poster = omdbResult.poster,
                backdrop = omdbResult.backdrop,
                overview = prefixOverview("OMDB", omdbResult.overview),
                originalLanguage = omdbResult.originalLanguage,
                originalTitle = omdbResult.originalTitle,
                popularity = omdbResult.popularity,
                releaseDate = omdbResult.releaseDate,
                voteAverage = omdbResult.voteAverage,
            )
            else -> null
        }
    }

    private fun buildMovie(tmdb: Movie, omdb: Movie): Movie {
        val combinedOverview = "${prefixOverview("TMDB", tmdb.overview)}\n${prefixOverview("OMDB", omdb.overview)}"

        val popularity = (tmdb.popularity + omdb.popularity) / 2.0
        val voteAverage = (tmdb.voteAverage + omdb.voteAverage) / 2.0

        val id = tmdb.id
        val title = tmdb.title.ifBlank { omdb.title }
        val poster = tmdb.poster.ifBlank { omdb.poster }
        val backdrop = tmdb.backdrop ?: omdb.backdrop
        val originalLanguage = tmdb.originalLanguage.ifBlank { omdb.originalLanguage }
        val originalTitle = tmdb.originalTitle.ifBlank { omdb.originalTitle }
        val releaseDate = tmdb.releaseDate.ifBlank { omdb.releaseDate }

        return Movie(
            id = id,
            title = title,
            poster = poster,
            backdrop = backdrop,
            overview = combinedOverview,
            originalLanguage = originalLanguage,
            originalTitle = originalTitle,
            popularity = popularity,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
        )
    }

    private fun prefixOverview(source: String, overview: String): String {
        val normalized = overview
            .removePrefix("TMDB: ")
            .removePrefix("OMDB: ")

        return if (normalized.isBlank()) normalized else "$source: $normalized"
    }
}


