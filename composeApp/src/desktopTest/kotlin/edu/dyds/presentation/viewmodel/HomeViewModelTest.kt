package edu.dyds.presentation.viewmodel

import edu.dyds.domain.entities.QualifiedMovie
import edu.dyds.domain.entities.Movie
import edu.dyds.domain.usecases.GetMoviesUseCase
import edu.dyds.presentation.home.HomeViewModel
import edu.dyds.testutils.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `getAllMovies emits initial loading and final populated state`() = runTest {
        val expectedMovies = listOf(
            qualifiedMovie(id = 1, isGoodMovie = true),
            qualifiedMovie(id = 2, isGoodMovie = false),
        )
        val resultGate = CompletableDeferred<List<QualifiedMovie>>()
        val useCase = SuspendedGetMoviesUseCase(resultGate)
        val viewModel = HomeViewModel(useCase)
        val initialState = viewModel.moviesStateFlow.first()

        assertFalse(initialState.isLoading)
        assertTrue(initialState.movies.isEmpty())

        viewModel.getAllMovies()
        runCurrent()

        val loadingState = viewModel.moviesStateFlow.first()

        assertEquals(1, useCase.invocationCount)
        assertTrue(loadingState.isLoading)
        assertTrue(loadingState.movies.isEmpty())

        resultGate.complete(expectedMovies)
        advanceUntilIdle()

        val finalState = viewModel.moviesStateFlow.first()

        assertFalse(finalState.isLoading)
        assertEquals(expectedMovies, finalState.movies)
    }

    @Test
    fun `getAllMovies emits final empty state when use case returns no movies`() = runTest {
        val useCase = FakeGetMoviesUseCase(emptyList())
        val viewModel = HomeViewModel(useCase)

        viewModel.getAllMovies()
        advanceUntilIdle()

        val finalState = viewModel.moviesStateFlow.first()

        assertEquals(1, useCase.invocationCount)
        assertFalse(finalState.isLoading)
        assertTrue(finalState.movies.isEmpty())
    }

    private class FakeGetMoviesUseCase(
        private val result: List<QualifiedMovie>,
    ) : GetMoviesUseCase {

        var invocationCount: Int = 0

        override suspend fun invoke(): List<QualifiedMovie> {
            invocationCount++
            return result
        }
    }

    private class SuspendedGetMoviesUseCase(
        private val resultGate: CompletableDeferred<List<QualifiedMovie>>,
    ) : GetMoviesUseCase {

        var invocationCount: Int = 0

        override suspend fun invoke(): List<QualifiedMovie> {
            invocationCount++
            return resultGate.await()
        }
    }

    private fun qualifiedMovie(id: Int, isGoodMovie: Boolean) = QualifiedMovie(
        movie = Movie(
            id = id,
            title = "Movie $id",
            poster = "poster-$id",
        ),
        isGoodMovie = isGoodMovie,
    )
}
