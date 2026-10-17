package com.cinecompare.aggregator.base

import com.cinecompare.aggregator.base.models.*
import com.cinecompare.core.common.Result
import java.time.LocalDate

interface MovieAggregator {
    val platform: Platform

    suspend fun getNowShowing(city: City): Result<List<Movie>>

    suspend fun getShowListings(
        movieId: String,
        city: City,
        date: LocalDate
    ): Result<List<ShowListing>>

    suspend fun getSeatAvailability(
        showId: String,
        cinema: Cinema
    ): Result<SeatLayout>

    fun getBookingDeepLink(showId: String, cinema: Cinema): String
}

data class ComparisonResult(
    val movie: Movie,
    val cinema: Cinema,
    val showTime: java.time.LocalDateTime,
    val format: String,
    val language: String,
    val listings: List<ShowListing>,
    val cheapestListing: ShowListing?,
    val mostExpensiveListing: ShowListing?,
    val savingsAmount: Int // paise saved vs most expensive
) {
    val hasDifference: Boolean get() = listings.size > 1 &&
        cheapestListing?.priceRange?.minPricePaise != mostExpensiveListing?.priceRange?.minPricePaise
}
