package edu.dyds.presentation.fakes

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.usecases.GetMovieDetailUseCase
import kotlinx.coroutines.yield

class FakeGetMovieDetailUseCase(
    private val result: Movie? = null,
) : GetMovieDetailUseCase {

    var invocationCount: Int = 0
    var requestedTitle: String? = null

    override suspend fun invoke(title: String): Movie? {
        invocationCount++
        requestedTitle = title
        yield()
        return result
    }
}
