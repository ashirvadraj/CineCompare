package com.cinecompare.app.di

import android.content.Context
import androidx.room.Room
import com.cinecompare.core.database.CineCompareDatabase
import com.cinecompare.core.database.dao.MovieDao
import com.cinecompare.core.database.dao.ShowListingDao
import com.cinecompare.core.network.OkHttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClientFactory.create(debug = true)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CineCompareDatabase =
        Room.databaseBuilder(
            context,
            CineCompareDatabase::class.java,
            "cinecompare.db"
        ).fallbackToDestructiveMigration().build()

    @Provides fun provideMovieDao(db: CineCompareDatabase): MovieDao = db.movieDao()
    @Provides fun provideShowListingDao(db: CineCompareDatabase): ShowListingDao = db.showListingDao()
}
