package edu.dyds.domain.entities

open class Movie(
	open val id: Int,
	open val title: String,
	open val poster: String,
	open val backdrop: String? = null,
	open val overview: String = "",
	open val originalLanguage: String = "",
	open val originalTitle: String = "",
	open val popularity: Double = 0.0,
	open val releaseDate: String = "",
	open val voteAverage: Double = 0.0,
)


