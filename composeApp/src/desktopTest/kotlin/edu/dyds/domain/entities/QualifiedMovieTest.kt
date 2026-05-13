package edu.dyds.domain.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QualifiedMovieTest {

    @Test
    fun `when built from a movie, the secondary constructor copies every inherited field and the good movie flag`() {
        val movie = Movie(
            id = 3,
            title = "Movie 3",
            poster = "poster-3",
            backdrop = "backdrop-3",
            overview = "Overview 3",
            originalLanguage = "es",
            originalTitle = "Original 3",
            popularity = 33.3,
            releaseDate = "2026-03-03",
            voteAverage = 8.3,
        )

        val result = QualifiedMovie(movie = movie, isGoodMovie = true)

        assertEquals(movie.id, result.id)
        assertEquals(movie.title, result.title)
        assertEquals(movie.poster, result.poster)
        assertEquals(movie.backdrop, result.backdrop)
        assertEquals(movie.overview, result.overview)
        assertEquals(movie.originalLanguage, result.originalLanguage)
        assertEquals(movie.originalTitle, result.originalTitle)
        assertEquals(movie.popularity, result.popularity)
        assertEquals(movie.releaseDate, result.releaseDate)
        assertEquals(movie.voteAverage, result.voteAverage)
        assertTrue(result.isGoodMovie)
    }
}
