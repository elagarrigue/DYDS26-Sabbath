package edu.dyds.domain.entities

data class Movie(
	val id: Int,
	val title: String,
	val poster: String,
	val backdrop: String? = null,
	val overview: String = "",
	val originalLanguage: String = "",
	val originalTitle: String = "",
	val popularity: Double = 0.0,
	val releaseDate: String = "",
	val voteAverage: Double = 0.0,
)

data class QualifiedMovie(
	val movie: Movie,
	val isGoodMovie: Boolean,
)


