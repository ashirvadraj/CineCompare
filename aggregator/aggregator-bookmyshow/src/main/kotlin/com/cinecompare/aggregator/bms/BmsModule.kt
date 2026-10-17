package com.cinecompare.aggregator.bms

import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.core.network.OkHttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BmsModule {
    @Provides
    @Singleton
    @IntoSet
    fun provideBookMyShowAggregator(okHttpClient: OkHttpClient): MovieAggregator =
        BookMyShowAggregator(okHttpClient)
}
