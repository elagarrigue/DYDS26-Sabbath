package edu.dyds.data.external.tmdb

import edu.dyds.data.external.tmdb.TMDBMovie
import edu.dyds.data.external.tmdb.toDomainMovie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RemoteMovieMappingTest {

    @Test
    fun `when remote movie has image paths, toDomainMovie builds full image urls`() {
        val remoteMovie = TMDBMovie(
            id = 5,
            title = "Movie 5",
            posterPath = "/poster5.jpg",
            backdropPath = "/backdrop5.jpg",
            overview = "Overview 5",
            originalLanguage = "en",
            originalTitle = "Original 5",
            popularity = 50.5,
            releaseDate = "2026-05-05",
            voteAverage = 7.5,
        )

        val result = remoteMovie.toDomainMovie()

        assertEquals(5, result.id)
        assertEquals("Movie 5", result.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster5.jpg", result.poster)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop5.jpg", result.backdrop)
        assertEquals("Overview 5", result.overview)
        assertEquals("en", result.originalLanguage)
        assertEquals("Original 5", result.originalTitle)
        assertEquals(50.5, result.popularity)
        assertEquals("2026-05-05", result.releaseDate)
        assertEquals(7.5, result.voteAverage)
    }

    @Test
    fun `when remote movie does not have image paths, toDomainMovie keeps poster empty and backdrop null`() {
        val remoteMovie = TMDBMovie(
            id = 8,
            title = "Movie 8",
            posterPath = null,
            backdropPath = null,
        )

        val result = remoteMovie.toDomainMovie()

        assertEquals("", result.poster)
        assertNull(result.backdrop)
    }
}


