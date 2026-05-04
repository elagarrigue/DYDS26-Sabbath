package edu.dyds.domain.usecases

import edu.dyds.domain.entities.QualifiedMovie
import edu.dyds.domain.repositories.MovieRepository

@Suppress("unused")
class GetMoviesUseCaseImpl(
    private val movieRepository: MovieRepository,
) : GetMoviesUseCase {
    override suspend fun invoke(): List<QualifiedMovie> {
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
