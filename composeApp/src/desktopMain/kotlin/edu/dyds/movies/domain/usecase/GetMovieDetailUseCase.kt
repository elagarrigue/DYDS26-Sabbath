package edu.dyds.movies.domain.usecase

import edu.dyds.movies.Movie
import edu.dyds.domain.usecases.GetMovieDetailUseCase as DomainGetMovieDetailUseCase

class GetMovieDetailUseCase(
    private val getMovieDetailUseCase: DomainGetMovieDetailUseCase,
) {
    suspend fun execute(id: Int): Movie? {
        return getMovieDetailUseCase(id)
    }
}
