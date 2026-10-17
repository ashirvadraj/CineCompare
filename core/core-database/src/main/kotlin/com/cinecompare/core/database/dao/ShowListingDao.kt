package com.cinecompare.core.database.dao

import androidx.room.*
import com.cinecompare.core.database.entities.ShowListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowListingDao {
    @Query("SELECT * FROM show_listings WHERE movieId = :movieId AND cityId = :cityId AND showDate = :date ORDER BY showTimeEpoch")
    fun getShowListings(movieId: String, cityId: String, date: String): Flow<List<ShowListingEntity>>

    @Query("SELECT * FROM show_listings WHERE movieId = :movieId AND cityId = :cityId AND showDate = :date")
    suspend fun getShowListingsSync(movieId: String, cityId: String, date: String): List<ShowListingEntity>

    @Upsert
    suspend fun upsertListings(listings: List<ShowListingEntity>)

    @Query("DELETE FROM show_listings WHERE showDate < :date")
    suspend fun deleteOldListings(date: String)
}
