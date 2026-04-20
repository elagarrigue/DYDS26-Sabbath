package edu.dyds.domain.repositories

import edu.dyds.domain.entities.Movie

interface IMovieRepository {
    suspend fun getMovies(): List<Movie>
    suspend fun getMovieDetail(id: Int): Movie?
}

