package edu.dyds.movies.domain.usecase

import edu.dyds.movies.QualifiedMovie
import edu.dyds.domain.usecases.GetMoviesUseCase as DomainGetMoviesUseCase

class GetMoviesUseCase(
    private val getMoviesUseCase: DomainGetMoviesUseCase,
) {
    suspend fun execute(): List<QualifiedMovie> {
        return getMoviesUseCase()
    }
}
