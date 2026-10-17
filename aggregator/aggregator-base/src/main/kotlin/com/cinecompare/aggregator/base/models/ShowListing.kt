package com.cinecompare.aggregator.base.models

import java.time.Instant
import java.time.LocalDateTime

data class ShowListing(
    val showId: String,
    val platform: Platform,
    val cinema: Cinema,
    val movie: Movie,
    val showTime: LocalDateTime,
    val format: String,        // 2D, 3D, IMAX, 4DX
    val language: String,
    val availableSeats: Int,
    val totalSeats: Int = 0,
    val priceRange: PriceRange,
    val seatCategories: List<SeatCategory> = emptyList(),
    val deepLinkUrl: String,
    val lastUpdatedAt: Instant = Instant.now(),
    val availability: SeatAvailability = SeatAvailability.AVAILABLE
)

data class PriceRange(
    val minPricePaise: Int,
    val maxPricePaise: Int,
    val currency: String = "INR"
) {
    val minPriceRupees: Int get() = minPricePaise / 100
    val maxPriceRupees: Int get() = maxPricePaise / 100
    val displayString: String get() = "₹$minPriceRupees – ₹$maxPriceRupees"
}

data class SeatCategory(
    val name: String,       // Recliner, Premium, Classic
    val pricePaise: Int,
    val available: Int,
    val total: Int
)

data class SeatLayout(
    val showId: String,
    val categories: List<SeatCategory>,
    val rows: List<SeatRow>
)

data class SeatRow(
    val label: String,
    val seats: List<Seat>
)

data class Seat(
    val id: String,
    val number: String,
    val category: String,
    val status: SeatStatus,
    val pricePaise: Int
)

enum class SeatStatus { AVAILABLE, BOOKED, BLOCKED, SELECTED }

enum class SeatAvailability {
    AVAILABLE,
    FILLING_FAST,  // < 30% seats left
    ALMOST_FULL,   // < 10% seats left
    HOUSEFULL
}
