package edu.dyds.presentation.fakes

import edu.dyds.domain.entities.QualifiedMovie
import edu.dyds.domain.usecases.GetMoviesUseCase
import kotlinx.coroutines.CompletableDeferred

class FakeGetMoviesUseCase(
    private val result: List<QualifiedMovie> = emptyList(),
    private val gate: CompletableDeferred<Unit>? = null,
) : GetMoviesUseCase {

    var invocationCount: Int = 0

    override suspend fun invoke(): List<QualifiedMovie> {
        invocationCount++
        gate?.await()
        return result
    }
}
