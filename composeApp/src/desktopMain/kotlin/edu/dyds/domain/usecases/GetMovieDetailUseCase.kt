package edu.dyds.domain.usecases

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.IMovieRepository

class GetMovieDetailUseCase(
    private val movieRepository: IMovieRepository,
) {
    suspend operator fun invoke(id: Int): Movie? {
        return movieRepository.getMovieDetail(id)
    }
}

