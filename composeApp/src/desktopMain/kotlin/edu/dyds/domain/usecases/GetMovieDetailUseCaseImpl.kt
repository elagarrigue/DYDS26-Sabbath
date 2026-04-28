package edu.dyds.domain.usecases

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.MovieRepository

@Suppress("unused")
class GetMovieDetailUseCaseImpl(
    private val movieRepository: MovieRepository,
) : GetMovieDetailUseCase {
    override suspend fun invoke(id: Int): Movie? {
        return movieRepository.getMovieDetail(id)
    }
}
