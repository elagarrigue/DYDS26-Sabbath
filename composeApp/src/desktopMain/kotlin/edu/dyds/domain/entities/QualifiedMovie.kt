package edu.dyds.domain.entities

data class QualifiedMovie(
    override val id: Int,
    override val title: String,
    override val poster: String,
    override val backdrop: String? = null,
    override val overview: String = "",
    override val originalLanguage: String = "",
    override val originalTitle: String = "",
    override val popularity: Double = 0.0,
    override val releaseDate: String = "",
    override val voteAverage: Double = 0.0,
    val isGoodMovie: Boolean,
) : Movie(
    id = id,
    title = title,
    poster = poster,
    backdrop = backdrop,
    overview = overview,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    popularity = popularity,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
) {
    constructor(movie: Movie, isGoodMovie: Boolean) : this(
        id = movie.id,
        title = movie.title,
        poster = movie.poster,
        backdrop = movie.backdrop,
        overview = movie.overview,
        originalLanguage = movie.originalLanguage,
        originalTitle = movie.originalTitle,
        popularity = movie.popularity,
        releaseDate = movie.releaseDate,
        voteAverage = movie.voteAverage,
        isGoodMovie = isGoodMovie,
    )
}

