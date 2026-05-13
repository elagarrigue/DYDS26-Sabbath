package edu.dyds.presentation.viewmodel

import app.cash.turbine.test
import edu.dyds.domain.entities.QualifiedMovie
import edu.dyds.presentation.home.HomeViewModel
import edu.dyds.presentation.fakes.FakeGetMoviesUseCase
import edu.dyds.testutils.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when use case result is pending and then available, getAllMovies emits loading and populated states`() = runTest {
        val expectedMovies = listOf(
            qualifiedMovie(id = 1, isGoodMovie = true),
            qualifiedMovie(id = 2, isGoodMovie = false),
        )
        val resultGate = CompletableDeferred<Unit>()
        val useCase = FakeGetMoviesUseCase(result = expectedMovies, gate = resultGate)
        val viewModel = HomeViewModel(useCase)

        viewModel.moviesStateFlow.test {
            assertEquals(HomeViewModel.MoviesUiState(), awaitItem())

            viewModel.getAllMovies()

            assertEquals(1, useCase.invocationCount)
            assertEquals(
                HomeViewModel.MoviesUiState(isLoading = true),
                awaitItem()
            )

            resultGate.complete(Unit)
            advanceUntilIdle()

            assertEquals(
                HomeViewModel.MoviesUiState(movies = expectedMovies),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when use case returns no movies, getAllMovies emits an empty final state`() = runTest {
        val useCase = FakeGetMoviesUseCase(result = emptyList())
        val viewModel = HomeViewModel(useCase)

        viewModel.getAllMovies()
        advanceUntilIdle()

        assertEquals(1, useCase.invocationCount)
        assertEquals(HomeViewModel.MoviesUiState(), viewModel.moviesStateFlow.value)
    }

    private fun qualifiedMovie(id: Int, isGoodMovie: Boolean) = QualifiedMovie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
        isGoodMovie = isGoodMovie,
    )
}
