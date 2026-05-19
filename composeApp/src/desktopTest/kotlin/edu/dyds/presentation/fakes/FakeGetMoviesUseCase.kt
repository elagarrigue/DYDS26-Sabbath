package edu.dyds.presentation.fakes

import edu.dyds.domain.entities.QualifiedMovie
import edu.dyds.domain.usecases.GetMoviesUseCase
import kotlinx.coroutines.yield

class FakeGetMoviesUseCase(
    private val result: List<QualifiedMovie> = emptyList(),
) : GetMoviesUseCase {

    var invocationCount: Int = 0

    override suspend fun invoke(): List<QualifiedMovie> {
        invocationCount++
        yield()
        return result
    }
}
