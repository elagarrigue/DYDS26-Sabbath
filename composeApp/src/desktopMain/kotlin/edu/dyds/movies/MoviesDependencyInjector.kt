package edu.dyds.movies

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.data.remote.MovieRemoteDataSource
import edu.dyds.data.repositories.MovieRepositoryImpl
import edu.dyds.domain.usecases.GetMovieDetailUseCase as DomainGetMovieDetailUseCase
import edu.dyds.domain.usecases.GetMoviesUseCase as DomainGetMoviesUseCase
import edu.dyds.movies.domain.usecase.GetMovieDetailUseCase
import edu.dyds.movies.domain.usecase.GetMoviesUseCase
import edu.dyds.movies.presentation.detail.DetailViewModel
import edu.dyds.movies.presentation.home.HomeViewModel
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
    private val domainGetMoviesUseCase = DomainGetMoviesUseCase(movieRepository)
    private val domainGetMovieDetailUseCase = DomainGetMovieDetailUseCase(movieRepository)

    private val getMoviesUseCase = GetMoviesUseCase(domainGetMoviesUseCase)
    private val getMovieDetailUseCase = GetMovieDetailUseCase(domainGetMovieDetailUseCase)

    @Composable
    fun getHomeViewModel(): HomeViewModel {
        return viewModel { HomeViewModel(getMoviesUseCase) }
    }

    @Composable
    fun getDetailViewModel(): DetailViewModel {
        return viewModel { DetailViewModel(getMovieDetailUseCase) }
    }
}
