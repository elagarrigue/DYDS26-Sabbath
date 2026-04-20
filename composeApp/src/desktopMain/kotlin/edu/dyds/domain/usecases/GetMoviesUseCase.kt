package edu.dyds.domain.usecases

import edu.dyds.domain.entities.QualifiedMovie
import edu.dyds.domain.repositories.IMovieRepository

class GetMoviesUseCase(
    private val movieRepository: IMovieRepository,
) {
    suspend operator fun invoke(): List<QualifiedMovie> {
        return movieRepository
            .getMovies()
            .sortedByDescending { it.popularity }
            .map { movie ->
                QualifiedMovie(
                    movie = movie,
                    isGoodMovie = movie.voteAverage >= 7.0,
                )
            }
    }
}

