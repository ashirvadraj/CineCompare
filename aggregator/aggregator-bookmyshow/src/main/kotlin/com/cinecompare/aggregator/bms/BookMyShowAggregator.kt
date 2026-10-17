package com.cinecompare.aggregator.bms

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
class BookMyShowAggregator(
    private val okHttpClient: OkHttpClient
) : MovieAggregator {

    override val platform = Platform.BOOK_MY_SHOW

    override suspend fun getNowShowing(city: City): Result<List<Movie>> = safeApiCall {
        val url = "${platform.baseUrl}/api/o/v2/movies" +
            "?status=nowShowing&city=${city.bmsCode}&count=25"
        val response = fetch(url)
        parseBmsMovies(response)
    }

    override suspend fun getShowListings(
        movieId: String,
        city: City,
        date: LocalDate
    ): Result<List<ShowListing>> = safeApiCall {
        val dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val url = "${platform.baseUrl}/api/o/v2/venues/" +
            "?eventCode=$movieId&regionCode=${city.bmsCode}&date=$dateStr&category=MOVIES"
        val response = fetch(url)
        parseBmsShowListings(response, movieId, city)
    }

    override suspend fun getSeatAvailability(
        showId: String,
        cinema: Cinema
    ): Result<SeatLayout> = safeApiCall {
        val url = "${platform.baseUrl}/api/o/v2/bookingpage/seatSelectionPage" +
            "?showCode=$showId"
        val response = fetch(url)
        parseBmsSeatLayout(response, showId)
    }

    override fun getBookingDeepLink(showId: String, cinema: Cinema): String =
        "${platform.baseUrl}/buytickets/$showId"

    private fun fetch(url: String): String {
        val request = Request.Builder()
            .url(url)
            .addHeader("x-bms-id", "application/json, text/javascript")
            .addHeader("Accept", "application/json")
            .build()
        return okHttpClient.newCall(request).execute().use { it.body?.string() ?: "" }
    }

    private fun parseBmsMovies(json: String): List<Movie> {
        return try {
            val root = JsonParser.parseString(json).asJsonObject
            val films = root.getAsJsonArray("BookMyShow")
                ?.get(0)?.asJsonObject
                ?.getAsJsonArray("films") ?: return emptyList()

            films.mapNotNull { el ->
                val film = el.asJsonObject
                try {
                    Movie(
                        id = film.get("filmCode")?.asString ?: return@mapNotNull null,
                        title = film.get("filmName")?.asString ?: return@mapNotNull null,
                        posterUrl = film.get("imageURL")?.asString ?: "",
                        genres = film.getAsJsonArray("genres")
                            ?.map { it.asString } ?: emptyList(),
                        durationMinutes = film.get("movieDuration")?.asInt ?: 0,
                        rating = film.get("wcTotalVotes")?.asFloat ?: 0f,
                        languages = film.getAsJsonArray("languages")
                            ?.map { it.asString } ?: emptyList(),
                        formats = film.getAsJsonArray("formats")
                            ?.map { it.asString } ?: emptyList(),
                        certification = film.get("censor")?.asString ?: "UA"
                    )
                } catch (e: Exception) { null }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseBmsShowListings(
        json: String,
        movieId: String,
        city: City
    ): List<ShowListing> {
        return try {
            val root = JsonParser.parseString(json).asJsonObject
            val venues = root.getAsJsonArray("BookMyShow")
                ?.get(0)?.asJsonObject
                ?.getAsJsonArray("Venues") ?: return emptyList()

            val listings = mutableListOf<ShowListing>()
            venues.forEach { venueEl ->
                val venue = venueEl.asJsonObject
                val cinemaName = venue.get("VenueName")?.asString ?: return@forEach
                val cinemaAddress = venue.get("VenueAddress")?.asString ?: ""
                val cinema = Cinema(
                    id = venue.get("VenueCode")?.asString ?: "",
                    name = cinemaName,
                    address = cinemaAddress,
                    city = city.name
                )

                venue.getAsJsonArray("ShowDetails")?.forEach showLoop@{ showEl ->
                    val show = showEl.asJsonObject
                    val showDate = show.get("ShowDate")?.asString ?: return@showLoop
                    val showTime = show.get("ShowTime")?.asString ?: return@showLoop
                    val showId = show.get("ShowCode")?.asString ?: return@showLoop
                    val format = show.get("ExperienceName")?.asString ?: "2D"
                    val language = show.get("LanguageName")?.asString ?: "Hindi"
                    val seatsAvail = show.get("SeatsAvail")?.asInt ?: 100
                    val minPrice = ((show.get("MinTicketPrice")?.asFloat ?: 0f) * 100).toInt()
                    val maxPrice = ((show.get("MaxTicketPrice")?.asFloat ?: 0f) * 100).toInt()

                    val showDateTime = try {
                        LocalDateTime.parse(
                            "$showDate $showTime",
                            DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm a")
                        )
                    } catch (e: Exception) {
                        LocalDateTime.now().plusHours(1)
                    }

                    listings.add(
                        ShowListing(
                            showId = showId,
                            platform = Platform.BOOK_MY_SHOW,
                            cinema = cinema,
                            movie = Movie(id = movieId, title = "", posterUrl = ""),
                            showTime = showDateTime,
                            format = format,
                            language = language,
                            availableSeats = seatsAvail,
                            priceRange = PriceRange(
                                minPricePaise = minPrice,
                                maxPricePaise = maxPrice
                            ),
                            deepLinkUrl = getBookingDeepLink(showId, cinema),
                            availability = when {
                                seatsAvail == 0 -> SeatAvailability.HOUSEFULL
                                seatsAvail < 10 -> SeatAvailability.ALMOST_FULL
                                seatsAvail < 30 -> SeatAvailability.FILLING_FAST
                                else -> SeatAvailability.AVAILABLE
                            }
                        )
                    )
                }
            }
            listings
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseBmsSeatLayout(json: String, showId: String): SeatLayout {
        return SeatLayout(showId = showId, categories = emptyList(), rows = emptyList())
    }
}
