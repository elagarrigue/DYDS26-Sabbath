package edu.dyds.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.data.local.MovieLocalDataSourceImpl
import edu.dyds.data.external.tmdb.TMDBMoviesExternalSource
import edu.dyds.data.repositoriesImpl.MovieRepositoryImpl
import edu.dyds.domain.usecases.GetMovieDetailUseCase
import edu.dyds.domain.usecases.GetMovieDetailUseCaseImpl
import edu.dyds.domain.usecases.GetMoviesUseCase
import edu.dyds.domain.usecases.GetMoviesUseCaseImpl
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import edu.dyds.data.external.omdb.OMDBMoviesExternalSourceImpl
import edu.dyds.data.external.MovieDetailsExternalSourceBroker

private const val TMDB_API_KEY = "d18da1b5da16397619c688b0263cd281"
private const val OMDB_API_KEY = "a96e7f78"

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
                    parameters.append("api_key", TMDB_API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
            }
        }

    private val tmdbMoviesExternalSource = TMDBMoviesExternalSource(tmdbHttpClient)

    private val omdbHttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
    }

    private val omdbMoviesExternalSource = OMDBMoviesExternalSourceImpl(
        httpClient = omdbHttpClient,
        apiKey = OMDB_API_KEY,
    )

    // Broker that delegates to TMDB and OMDb and combines results when both are available
    private val movieDetailsBroker = MovieDetailsExternalSourceBroker(
        tmdbSource = tmdbMoviesExternalSource,
        omdbSource = omdbMoviesExternalSource,
    )

    private val movieLocalDataSource = MovieLocalDataSourceImpl()
    private val movieRepository = MovieRepositoryImpl(
        popularMoviesSource = tmdbMoviesExternalSource,
        detailsSource = movieDetailsBroker,
        movieLocalDataSource = movieLocalDataSource
    )
    private val getMoviesUseCase: GetMoviesUseCase = GetMoviesUseCaseImpl(movieRepository)
    private val getMovieDetailUseCase: GetMovieDetailUseCase = GetMovieDetailUseCaseImpl(movieRepository)

    @Composable
    fun provideHomeViewModel(): edu.dyds.presentation.home.HomeViewModel {
        return viewModel { edu.dyds.presentation.home.HomeViewModel(getMoviesUseCase) }
    }

    @Composable
    fun provideDetailViewModel(): edu.dyds.presentation.detail.DetailViewModel {
        return viewModel { edu.dyds.presentation.detail.DetailViewModel(getMovieDetailUseCase) }
    }
}
