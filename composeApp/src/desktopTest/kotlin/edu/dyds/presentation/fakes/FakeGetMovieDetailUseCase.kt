package edu.dyds.presentation.fakes

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.usecases.GetMovieDetailUseCase
import kotlinx.coroutines.CompletableDeferred

class FakeGetMovieDetailUseCase(
    private val result: Movie? = null,
    private val gate: CompletableDeferred<Unit>? = null,
) : GetMovieDetailUseCase {

    var invocationCount: Int = 0
    var requestedId: Int? = null

    override suspend fun invoke(id: Int): Movie? {
        invocationCount++
        requestedId = id
        gate?.await()
        return result
    }
}
