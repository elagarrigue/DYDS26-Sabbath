package edu.dyds.data.repositories

import edu.dyds.data.remote.MovieRemoteDataSource
import edu.dyds.data.remote.toDomainMovie
import edu.dyds.domain.entities.Movie
import edu.dyds.domain.repositories.IMovieRepository

class MovieRepositoryImpl(
	private val movieRemoteDataSource: MovieRemoteDataSource,
) : IMovieRepository {
	override suspend fun getMovies(): List<Movie> {
		return movieRemoteDataSource
			.getPopularMovies()
			.map { it.toDomainMovie() }
	}

	override suspend fun getMovieDetail(id: Int): Movie? {
		return movieRemoteDataSource.getMovieDetail(id)?.toDomainMovie()
	}
}

