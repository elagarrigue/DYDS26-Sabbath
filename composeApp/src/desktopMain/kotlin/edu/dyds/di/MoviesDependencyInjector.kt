package edu.dyds.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.data.remote.MovieRemoteDataSource
import edu.dyds.data.repositories.MovieRepositoryImpl
import edu.dyds.domain.usecases.GetMovieDetailUseCase
import edu.dyds.domain.usecases.GetMoviesUseCase
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val API_KEY = "d18da1b5da16397619c688b0263cd281"

object MoviesDependencyInjector {

    private val tmdbHttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.themoviedb.org"
                    parameters.append("api_key", API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }

    private val movieRemoteDataSource = MovieRemoteDataSource(tmdbHttpClient)
    private val movieRepository = MovieRepositoryImpl(movieRemoteDataSource)
    private val getMoviesUseCase = GetMoviesUseCase(movieRepository)
    private val getMovieDetailUseCase = GetMovieDetailUseCase(movieRepository)

    @Composable
    fun provideHomeViewModel(): edu.dyds.presentation.viewmodel.HomeViewModel {
        return viewModel { edu.dyds.presentation.viewmodel.HomeViewModel(getMoviesUseCase) }
    }

    @Composable
    fun provideDetailViewModel(): edu.dyds.presentation.viewmodel.DetailViewModel {
        return viewModel { edu.dyds.presentation.viewmodel.DetailViewModel(getMovieDetailUseCase) }
    }
}

