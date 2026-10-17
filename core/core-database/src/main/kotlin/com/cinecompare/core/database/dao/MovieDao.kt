package com.cinecompare.core.database.dao

import androidx.room.*
import com.cinecompare.core.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY rating DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: String): MovieEntity?

    @Query("SELECT * FROM movies WHERE isWatchlisted = 1")
    fun getWatchlistedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE hasPriceAlert = 1")
    suspend fun getPriceAlertMovies(): List<MovieEntity>

    @Upsert
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Upsert
    suspend fun upsertMovie(movie: MovieEntity)

    @Query("UPDATE movies SET isWatchlisted = :watchlisted WHERE id = :movieId")
    suspend fun setWatchlisted(movieId: String, watchlisted: Boolean)

    @Query("UPDATE movies SET hasPriceAlert = :alert WHERE id = :movieId")
    suspend fun setPriceAlert(movieId: String, alert: Boolean)

    @Query("DELETE FROM movies WHERE isWatchlisted = 0 AND lastFetchedAt < :cutoff")
    suspend fun deleteStaleMovies(cutoff: Long)
}
