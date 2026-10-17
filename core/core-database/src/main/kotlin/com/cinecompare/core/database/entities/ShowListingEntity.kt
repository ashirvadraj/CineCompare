package com.cinecompare.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show_listings")
data class ShowListingEntity(
    @PrimaryKey val showId: String,
    val platformName: String,
    val cinemaId: String,
    val cinemaName: String,
    val cinemaAddress: String,
    val lat: Double,
    val lng: Double,
    val movieId: String,
    val movieTitle: String,
    val showTimeEpoch: Long,
    val format: String,
    val language: String,
    val availableSeats: Int,
    val minPricePaise: Int,
    val maxPricePaise: Int,
    val deepLinkUrl: String,
    val cityId: String,
    val showDate: String, // yyyy-MM-dd
    val lastUpdatedAt: Long = System.currentTimeMillis()
)
