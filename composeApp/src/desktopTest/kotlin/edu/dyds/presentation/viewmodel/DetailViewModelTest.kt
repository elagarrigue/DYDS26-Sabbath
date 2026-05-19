package edu.dyds.presentation.viewmodel

import app.cash.turbine.test
import edu.dyds.domain.entities.Movie
import edu.dyds.presentation.detail.DetailViewModel
import edu.dyds.presentation.fakes.FakeGetMovieDetailUseCase
import edu.dyds.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when use case returns movie, getMovieDetail emits loading and loaded states`() = runTest {
        val expectedMovie = movie(id = 7)
        val useCase = FakeGetMovieDetailUseCase(result = expectedMovie)
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

            assertEquals(
                DetailViewModel.MovieDetailUiState(movie = expectedMovie),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when use case returns null, getMovieDetail emits loading and empty final states`() = runTest {
        val useCase = FakeGetMovieDetailUseCase(result = null)
        val viewModel = DetailViewModel(useCase)

        viewModel.movieDetailStateFlow.test {
            assertEquals(DetailViewModel.MovieDetailUiState(), awaitItem())

            viewModel.getMovieDetail(42)

            assertEquals(1, useCase.invocationCount)
            assertEquals(42, useCase.requestedId)
            assertEquals(
                DetailViewModel.MovieDetailUiState(isLoading = true),
                awaitItem()
            )
            assertEquals(
                DetailViewModel.MovieDetailUiState(),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
