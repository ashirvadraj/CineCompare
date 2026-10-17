package com.cinecompare.aggregator.district

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
object DistrictModule {
    @Provides
    @Singleton
    @IntoSet
    fun provideDistrictAggregator(okHttpClient: OkHttpClient): MovieAggregator =
        DistrictAggregator(okHttpClient)
}
