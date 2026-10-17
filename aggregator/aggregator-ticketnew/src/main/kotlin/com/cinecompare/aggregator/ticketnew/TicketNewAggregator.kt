package com.cinecompare.aggregator.ticketnew

import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.aggregator.base.models.*
import com.cinecompare.core.common.Result
import com.cinecompare.core.common.safeApiCall
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TicketNewAggregator @Inject constructor(
    private val okHttpClient: OkHttpClient
) : MovieAggregator {

    override val platform = Platform.TICKET_NEW

    override suspend fun getNowShowing(city: City): Result<List<Movie>> = safeApiCall {
        val url = "${platform.baseUrl}/online-movies/${city.name.lowercase()}"
        val html = fetch(url)
        parseTicketNewMovies(html)
    }

    override suspend fun getShowListings(
        movieId: String,
        city: City,
        date: LocalDate
    ): Result<List<ShowListing>> = safeApiCall {
        val url = "${platform.baseUrl}/online-movie/${movieId}/${city.name.lowercase()}"
        val html = fetch(url)
        parseTicketNewShowtimes(html, movieId, city)
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
        val request = Request.Builder().url(url).build()
        return okHttpClient.newCall(request).execute().use { it.body?.string() ?: "" }
    }

    private fun parseTicketNewMovies(html: String): List<Movie> {
        val doc = Jsoup.parse(html)
        return doc.select(".movie-card").mapNotNull { card ->
            try {
                val title = card.select(".movie-title").text().trim()
                val id = card.select("a").attr("href").split("/").lastOrNull() ?: ""
                val poster = card.select("img").attr("src")
                if (title.isEmpty() || id.isEmpty()) return@mapNotNull null
                Movie(id = id, title = title, posterUrl = poster)
            } catch (e: Exception) { null }
        }
    }

    private fun parseTicketNewShowtimes(
        html: String,
        movieId: String,
        city: City
    ): List<ShowListing> {
        val doc = Jsoup.parse(html)
        val listings = mutableListOf<ShowListing>()
        doc.select(".theatre-section").forEach { theatreSection ->
            val cinemaName = theatreSection.select(".theatre-name").text()
            val cinema = Cinema(
                id = theatreSection.attr("data-id").ifEmpty { cinemaName },
                name = cinemaName,
                address = theatreSection.select(".theatre-address").text(),
                city = city.name
            )
            theatreSection.select(".show-time-btn").forEach { btn ->
                val showId = btn.attr("data-show-id").ifEmpty { return@forEach }
                val timeStr = btn.text().trim()
                listings.add(ShowListing(
                    showId = showId,
                    platform = Platform.TICKET_NEW,
                    cinema = cinema,
                    movie = Movie(id = movieId, title = "", posterUrl = ""),
                    showTime = LocalDateTime.now().plusHours(2),
                    format = "2D",
                    language = "Hindi",
                    availableSeats = 50,
                    priceRange = PriceRange(minPricePaise = 15000, maxPricePaise = 35000),
                    deepLinkUrl = getBookingDeepLink(showId, cinema)
                ))
            }
        }
        return listings
    }
}
