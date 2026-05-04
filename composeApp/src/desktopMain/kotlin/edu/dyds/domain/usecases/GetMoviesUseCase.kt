package edu.dyds.domain.usecases

import edu.dyds.domain.entities.QualifiedMovie

interface GetMoviesUseCase {
    suspend operator fun invoke(): List<QualifiedMovie>
}
