package com.cinecompare.aggregator.ticketnew

import com.cinecompare.aggregator.base.MovieAggregator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TicketNewModule {
    @Provides
    @Singleton
    @IntoSet
    fun provideTicketNewAggregator(okHttpClient: OkHttpClient): MovieAggregator =
        TicketNewAggregator(okHttpClient)
}
