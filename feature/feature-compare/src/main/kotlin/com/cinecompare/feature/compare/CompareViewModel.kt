package com.cinecompare.feature.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.aggregator.base.models.*
import com.cinecompare.aggregator.base.ComparisonResult
import com.cinecompare.core.database.dao.MovieDao
import com.cinecompare.core.database.dao.ShowListingDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class CompareUiState(
    val isLoading: Boolean = false,
    val comparisons: List<ComparisonResult> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedFormat: String = "All",
    val selectedCity: City? = null,
    val error: String? = null
)

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val aggregators: Set<@JvmSuppressWildcards MovieAggregator>,
    private val movieDao: MovieDao
) : ViewModel() {

    private val _state = MutableStateFlow(CompareUiState())
    val state: StateFlow<CompareUiState> = _state.asStateFlow()
    private var currentMovieId: String = ""

    fun loadForMovie(movieId: String) {
        currentMovieId = movieId
        val city = _state.value.selectedCity ?: POPULAR_CITIES.first()
        fetchComparisons(movieId, city, _state.value.selectedDate)
    }

    fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
        if (currentMovieId.isNotEmpty()) {
            fetchComparisons(currentMovieId, _state.value.selectedCity ?: POPULAR_CITIES.first(), date)
        }
    }

    fun selectFormat(format: String) {
        _state.update { it.copy(selectedFormat = format) }
        applyFilters()
    }

    fun refresh(movieId: String) = loadForMovie(movieId)

    private fun applyFilters() {
        // filter cached comparisons if needed
    }

    private fun fetchComparisons(movieId: String, city: City, date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Fetch from all platforms in parallel
                val platformResults = aggregators.map { aggregator ->
                    async {
                        try {
                            aggregator.getShowListings(movieId, city, date).getOrNull() ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                }.awaitAll()

                // Group by cinema name + hour:minute + format + language
                val allListings = platformResults.flatten()
                val grouped = allListings.groupBy { listing ->
                    "${listing.cinema.name}|${listing.showTime.hour}:${listing.showTime.minute}|${listing.format}|${listing.language}"
                }

                val comparisons = grouped.mapNotNull { (_, listings) ->
                    if (listings.isEmpty()) return@mapNotNull null
                    val first = listings.first()
                    val cheapest = listings.minByOrNull { it.priceRange.minPricePaise }
                    val mostExpensive = listings.maxByOrNull { it.priceRange.minPricePaise }
                    val savings = (mostExpensive?.priceRange?.minPricePaise ?: 0) -
                        (cheapest?.priceRange?.minPricePaise ?: 0)

                    ComparisonResult(
                        movie = first.movie,
                        cinema = first.cinema,
                        showTime = first.showTime,
                        format = first.format,
                        language = first.language,
                        listings = listings,
                        cheapestListing = cheapest,
                        mostExpensiveListing = mostExpensive,
                        savingsAmount = savings
                    )
                }.sortedBy { it.showTime }

                _state.update {
                    it.copy(
                        isLoading = false,
                        comparisons = comparisons
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to fetch comparisons"
                    )
                }
            }
        }
    }
}
