package com.cinecompare.app.di

import android.content.Context
import androidx.room.Room
import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.aggregator.bms.BookMyShowAggregator
import com.cinecompare.aggregator.district.DistrictAggregator
import com.cinecompare.aggregator.ticketnew.TicketNewAggregator
import com.cinecompare.core.database.CineCompareDatabase
import com.cinecompare.core.database.dao.MovieDao
import com.cinecompare.core.database.dao.ShowListingDao
import com.cinecompare.core.network.OkHttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
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

    @Provides
    @Singleton
    @IntoSet
    fun provideBookMyShowAggregator(client: OkHttpClient): MovieAggregator =
        BookMyShowAggregator(client)

    @Provides
    @Singleton
    @IntoSet
    fun provideDistrictAggregator(client: OkHttpClient): MovieAggregator =
        DistrictAggregator(client)

    @Provides
    @Singleton
    @IntoSet
    fun provideTicketNewAggregator(client: OkHttpClient): MovieAggregator =
        TicketNewAggregator(client)
}
