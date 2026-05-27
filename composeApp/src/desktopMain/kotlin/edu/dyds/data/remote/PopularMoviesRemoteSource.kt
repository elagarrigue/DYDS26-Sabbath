package edu.dyds.data.remote

import edu.dyds.data.external.PopularMoviesExternalSource
import edu.dyds.domain.entities.Movie

@Suppress("unused")
interface PopularMoviesRemoteSource : PopularMoviesExternalSource {
    override suspend fun getPopularMovies(): List<Movie>
}

