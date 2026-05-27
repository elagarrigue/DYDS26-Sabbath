package edu.dyds.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.data.local.MovieLocalDataSourceImpl
import edu.dyds.data.external.tmdb.TMDBMoviesExternalSourceImpl
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
import edu.dyds.data.remote.MovieDetailsRemoteSourceBroker
import edu.dyds.data.remote.tmdb.TMDBMoviesRemoteSourceImpl
import java.io.File
import java.util.Properties

private const val TMDB_API_KEY_NAME = "TMDB_API_KEY"
private const val OMDB_API_KEY_NAME = "OMDB_API_KEY"

object MoviesDependencyInjector {
    private val localProperties: Properties by lazy { loadLocalProperties() }
    private val tmdbApiKey = readApiKey(TMDB_API_KEY_NAME)
    private val omdbApiKey = readApiKey(OMDB_API_KEY_NAME)

    private fun readApiKey(name: String): String {
        return System.getenv(name)
            ?.takeIf { it.isNotBlank() }
            ?: System.getProperty(name)
                ?.takeIf { it.isNotBlank() }
            ?: localProperties.getProperty(name)
                ?.takeIf { it.isNotBlank() }
            ?: ""
    }

    private fun loadLocalProperties(): Properties {
        val properties = Properties()
        val candidates = listOf(
            File("local.properties"),
            File("../local.properties"),
        )
        val file = candidates.firstOrNull { it.exists() && it.isFile } ?: return properties
        file.inputStream().use { properties.load(it) }
        return properties
    }

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
                    parameters.append("api_key", tmdbApiKey)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }

    private val tmdbMoviesExternalSource = TMDBMoviesExternalSourceImpl(tmdbHttpClient)

    // Create a RemoteSource adapter for TMDB
    private val tmdbMoviesRemoteSource = TMDBMoviesRemoteSourceImpl(tmdbHttpClient)

    private val omdbHttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    private val omdbMoviesExternalSource = OMDBMoviesExternalSourceImpl(
        httpClient = omdbHttpClient,
        apiKey = omdbApiKey,
    )

    // Broker that delegates to TMDB and OMDb and combines results when both are available
    private val movieDetailsBroker = MovieDetailsRemoteSourceBroker(
        tmdbSource = tmdbMoviesExternalSource,
        omdbSource = omdbMoviesExternalSource,
    )

    private val movieLocalDataSource = MovieLocalDataSourceImpl()
    private val movieRepository = MovieRepositoryImpl(
        popularMoviesSource = tmdbMoviesRemoteSource,
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
