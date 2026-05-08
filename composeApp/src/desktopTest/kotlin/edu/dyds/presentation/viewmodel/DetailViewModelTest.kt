package edu.dyds.presentation.viewmodel

import edu.dyds.domain.entities.Movie
import edu.dyds.domain.usecases.GetMovieDetailUseCase
import edu.dyds.presentation.detail.DetailViewModel
import edu.dyds.testutils.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: GetMovieDetailUseCase
    private lateinit var viewModel: DetailViewModel

    @BeforeTest
    fun setUp() {
        useCase = FakeGetMovieDetailUseCase(null)
        viewModel = DetailViewModel(useCase)
    }

    @Test
    fun `getMovieDetail emits initial loading and final populated state`() = runTest {
        val expectedMovie = movie(id = 7)
        val resultGate = CompletableDeferred<Movie?>()
        useCase = SuspendedGetMovieDetailUseCase(resultGate)
        viewModel = DetailViewModel(useCase)
        val initialState = viewModel.movieDetailStateFlow.first()

        assertFalse(initialState.isLoading)
        assertNull(initialState.movie)

        viewModel.getMovieDetail(7)
        runCurrent()

        val loadingState = viewModel.movieDetailStateFlow.first()

        assertEquals(1, (useCase as SuspendedGetMovieDetailUseCase).invocationCount)
        assertEquals(7, (useCase as SuspendedGetMovieDetailUseCase).requestedId)
        assertTrue(loadingState.isLoading)
        assertNull(loadingState.movie)

        resultGate.complete(expectedMovie)
        advanceUntilIdle()

        val finalState = viewModel.movieDetailStateFlow.first()

        assertFalse(finalState.isLoading)
        assertEquals(expectedMovie, finalState.movie)
    }

    @Test
    fun `getMovieDetail emits final null movie when use case returns null`() = runTest {
        useCase = FakeGetMovieDetailUseCase(null)
        viewModel = DetailViewModel(useCase)

        viewModel.getMovieDetail(42)
        advanceUntilIdle()

        val finalState = viewModel.movieDetailStateFlow.first()

        assertEquals(1, (useCase as FakeGetMovieDetailUseCase).invocationCount)
        assertEquals(42, (useCase as FakeGetMovieDetailUseCase).requestedId)
        assertFalse(finalState.isLoading)
        assertNull(finalState.movie)
    }

    private class FakeGetMovieDetailUseCase(
        private val result: Movie?,
    ) : GetMovieDetailUseCase {

        var invocationCount: Int = 0
        var requestedId: Int? = null

        override suspend fun invoke(id: Int): Movie? {
            invocationCount++
            requestedId = id
            return result
        }
    }

    private class SuspendedGetMovieDetailUseCase(
        private val resultGate: CompletableDeferred<Movie?>,
    ) : GetMovieDetailUseCase {

        var invocationCount: Int = 0
        var requestedId: Int? = null

        override suspend fun invoke(id: Int): Movie? {
            invocationCount++
            requestedId = id
            return resultGate.await()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun movie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        poster = "poster-$id",
    )
}
