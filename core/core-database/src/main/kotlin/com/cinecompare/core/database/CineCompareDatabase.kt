package com.cinecompare.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cinecompare.core.database.dao.MovieDao
import com.cinecompare.core.database.dao.ShowListingDao
import com.cinecompare.core.database.entities.MovieEntity
import com.cinecompare.core.database.entities.ShowListingEntity

@Database(
    entities = [MovieEntity::class, ShowListingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CineCompareDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun showListingDao(): ShowListingDao
}
