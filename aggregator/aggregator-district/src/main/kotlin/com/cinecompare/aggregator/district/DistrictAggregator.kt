package com.cinecompare.aggregator.district

import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.aggregator.base.models.*
import com.cinecompare.core.common.Result
import com.cinecompare.core.common.safeApiCall
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
class DistrictAggregator(
    private val okHttpClient: OkHttpClient
) : MovieAggregator {

    override val platform = Platform.DISTRICT

    override suspend fun getNowShowing(city: City): Result<List<Movie>> = safeApiCall {
        val url = "${platform.baseUrl}/api/v1/movies/now-showing?city=${city.districtCode}"
        val response = fetch(url)
        parseDistrictMovies(response)
    }

    override suspend fun getShowListings(
        movieId: String,
        city: City,
        date: LocalDate
    ): Result<List<ShowListing>> = safeApiCall {
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val url = "${platform.baseUrl}/api/v1/movies/$movieId/showtimes" +
            "?city=${city.districtCode}&date=$dateStr"
        val response = fetch(url)
        parseDistrictShowtimes(response, movieId, city)
    }

    override suspend fun getSeatAvailability(
        showId: String,
        cinema: Cinema
    ): Result<SeatLayout> = safeApiCall {
        SeatLayout(showId = showId, categories = emptyList(), rows = emptyList())
    }

    override fun getBookingDeepLink(showId: String, cinema: Cinema): String =
        "${platform.baseUrl}/buytickets/$showId"

    private fun fetch(url: String): String {
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Origin", platform.baseUrl)
            .build()
        return okHttpClient.newCall(request).execute().use { it.body?.string() ?: "" }
    }

    private fun parseDistrictMovies(json: String): List<Movie> {
        return try {
            val arr = JsonParser.parseString(json).asJsonArray
            arr.mapNotNull { el ->
                val obj = el.asJsonObject
                try {
                    Movie(
                        id = obj.get("id")?.asString ?: return@mapNotNull null,
                        title = obj.get("name")?.asString ?: return@mapNotNull null,
                        posterUrl = obj.get("poster_url")?.asString ?: "",
                        genres = obj.getAsJsonArray("genres")
                            ?.map { it.asString } ?: emptyList(),
                        durationMinutes = obj.get("duration")?.asInt ?: 0,
                        rating = obj.get("rating")?.asFloat ?: 0f,
                        languages = obj.getAsJsonArray("languages")
                            ?.map { it.asString } ?: emptyList()
                    )
                } catch (e: Exception) { null }
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseDistrictShowtimes(
        json: String,
        movieId: String,
        city: City
    ): List<ShowListing> {
        return try {
            val root = JsonParser.parseString(json).asJsonObject
            val venues = root.getAsJsonArray("venues") ?: return emptyList()
            val listings = mutableListOf<ShowListing>()

            venues.forEach { venueEl ->
                val venue = venueEl.asJsonObject
                val cinema = Cinema(
                    id = venue.get("id")?.asString ?: "",
                    name = venue.get("name")?.asString ?: "",
                    address = venue.get("address")?.asString ?: "",
                    city = city.name,
                    lat = venue.get("lat")?.asDouble ?: 0.0,
                    lng = venue.get("lng")?.asDouble ?: 0.0
                )

                venue.getAsJsonArray("shows")?.forEach showLoop@{ showEl ->
                    val show = showEl.asJsonObject
                    val showId = show.get("id")?.asString ?: return@showLoop
                    val showTimeStr = show.get("start_time")?.asString ?: return@showLoop
                    val format = show.get("format")?.asString ?: "2D"
                    val language = show.get("language")?.asString ?: "Hindi"
                    val seatsLeft = show.get("seats_left")?.asInt ?: 100
                    val minPrice = ((show.get("min_price")?.asFloat ?: 0f) * 100).toInt()
                    val maxPrice = ((show.get("max_price")?.asFloat ?: 0f) * 100).toInt()

                    val showTime = try {
                        LocalDateTime.parse(showTimeStr, DateTimeFormatter.ISO_DATE_TIME)
                    } catch (e: Exception) { LocalDateTime.now() }

                    listings.add(ShowListing(
                        showId = showId,
                        platform = Platform.DISTRICT,
                        cinema = cinema,
                        movie = Movie(id = movieId, title = "", posterUrl = ""),
                        showTime = showTime,
                        format = format,
                        language = language,
                        availableSeats = seatsLeft,
                        priceRange = PriceRange(minPricePaise = minPrice, maxPricePaise = maxPrice),
                        deepLinkUrl = getBookingDeepLink(showId, cinema),
                        availability = when {
                            seatsLeft == 0 -> SeatAvailability.HOUSEFULL
                            seatsLeft < 10 -> SeatAvailability.ALMOST_FULL
                            seatsLeft < 30 -> SeatAvailability.FILLING_FAST
                            else -> SeatAvailability.AVAILABLE
                        }
                    ))
                }
            }
            listings
        } catch (e: Exception) { emptyList() }
    }
}
