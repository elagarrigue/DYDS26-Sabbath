package edu.dyds.data.remote

import edu.dyds.domain.entities.Movie

@Suppress("unused")
interface PopularMoviesRemoteSource : edu.dyds.data.external.PopularMoviesExternalSource {
    override suspend fun getPopularMovies(): List<Movie>
}

