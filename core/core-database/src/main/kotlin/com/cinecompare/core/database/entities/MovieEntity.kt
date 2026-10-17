package com.cinecompare.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val posterUrl: String,
    val bannerUrl: String,
    val genresJson: String,
    val durationMinutes: Int,
    val rating: Float,
    val releaseDateEpoch: Long,
    val languagesJson: String,
    val formatsJson: String,
    val trailerUrl: String?,
    val synopsis: String,
    val certification: String,
    val isWatchlisted: Boolean = false,
    val hasPriceAlert: Boolean = false,
    val lastFetchedAt: Long = System.currentTimeMillis()
)
