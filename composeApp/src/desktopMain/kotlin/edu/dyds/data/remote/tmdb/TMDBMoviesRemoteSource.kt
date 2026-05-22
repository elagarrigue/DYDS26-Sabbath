package edu.dyds.data.remote.tmdb

import edu.dyds.data.remote.MovieDetailsRemoteSource
import edu.dyds.data.remote.PopularMoviesRemoteSource
import edu.dyds.domain.entities.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface TMDBMoviesRemoteSource : PopularMoviesRemoteSource, MovieDetailsRemoteSource {
    override suspend fun getPopularMovies(): List<Movie>
    override suspend fun searchMovieByTitle(title: String): Movie?
}

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

fun TMDBMovie.toDomainMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        poster = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
        backdrop = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
        overview = overview,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
    )
}


