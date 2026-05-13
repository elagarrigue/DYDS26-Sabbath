package edu.dyds.presentation.viewmodel

import app.cash.turbine.test
import edu.dyds.domain.entities.Movie
import edu.dyds.presentation.detail.DetailViewModel
import edu.dyds.presentation.fakes.FakeGetMovieDetailUseCase
import edu.dyds.testutils.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when use case result is pending and then available, getMovieDetail emits loading and loaded states`() = runTest {
        val expectedMovie = movie(id = 7)
        val resultGate = CompletableDeferred<Unit>()
        val useCase = FakeGetMovieDetailUseCase(result = expectedMovie, gate = resultGate)
        val viewModel = DetailViewModel(useCase)

        viewModel.movieDetailStateFlow.test {
            assertEquals(DetailViewModel.MovieDetailUiState(), awaitItem())

            viewModel.getMovieDetail(7)

            assertEquals(1, useCase.invocationCount)
            assertEquals(7, useCase.requestedId)
            assertEquals(
                DetailViewModel.MovieDetailUiState(isLoading = true),
                awaitItem()
            )

            resultGate.complete(Unit)
            advanceUntilIdle()

            assertEquals(
                DetailViewModel.MovieDetailUiState(movie = expectedMovie),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when use case returns null, getMovieDetail emits an empty final state`() = runTest {
        val useCase = FakeGetMovieDetailUseCase(result = null)
        val viewModel = DetailViewModel(useCase)

        viewModel.getMovieDetail(42)
        advanceUntilIdle()

        assertEquals(1, useCase.invocationCount)
        assertEquals(42, useCase.requestedId)
        assertEquals(DetailViewModel.MovieDetailUiState(), viewModel.movieDetailStateFlow.value)
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
