package edu.dyds.domain.usecases

import edu.dyds.domain.entities.Movie

interface GetMovieDetailUseCase {
    suspend operator fun invoke(title: String): Movie?
}
