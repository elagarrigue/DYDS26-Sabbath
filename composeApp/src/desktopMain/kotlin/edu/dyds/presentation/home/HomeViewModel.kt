package edu.dyds.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.domain.entities.QualifiedMovie
import edu.dyds.domain.usecases.GetMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
) : ViewModel() {

    private val moviesStateMutableStateFlow = MutableStateFlow(MoviesUiState())

    val moviesStateFlow: StateFlow<MoviesUiState> = moviesStateMutableStateFlow.asStateFlow()

    fun getAllMovies() {
        viewModelScope.launch {
            try {
                moviesStateMutableStateFlow.emit(
                    MoviesUiState(isLoading = true)
                )
                moviesStateMutableStateFlow.emit(
                    MoviesUiState(
                        isLoading = false,
                        movies = getMoviesUseCase()
                    )
                )
            } catch (e: Throwable) {
                moviesStateMutableStateFlow.emit(
                    MoviesUiState(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load movies"
                    )
                )
            }
        }
    }

    data class MoviesUiState(
        val isLoading: Boolean = false,
        val movies: List<QualifiedMovie> = emptyList(),
        val errorMessage: String? = null,
    )
}




