package com.cinecompare.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.aggregator.base.models.City
import com.cinecompare.aggregator.base.models.Movie
import com.cinecompare.aggregator.base.models.POPULAR_CITIES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val selectedCity: City? = null,
    val selectedGenre: String = "All",
    val availableCities: List<City> = POPULAR_CITIES,
    val showCitySelector: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val aggregators: Set<@JvmSuppressWildcards MovieAggregator>
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun showCitySelector() = _state.update { it.copy(showCitySelector = true) }
    fun dismissCitySelector() = _state.update { it.copy(showCitySelector = false) }

    fun selectCity(city: City) {
        _state.update { it.copy(selectedCity = city, showCitySelector = false) }
        loadMovies(city)
    }

    fun selectGenre(genre: String) = _state.update { it.copy(selectedGenre = genre) }

    fun retry() {
        _state.value.selectedCity?.let { loadMovies(it) }
    }

    private fun loadMovies(city: City) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val allMovies = aggregators.map { aggregator ->
                    async { aggregator.getNowShowing(city).getOrNull() ?: emptyList() }
                }.awaitAll().flatten().distinctBy { it.title }

                _state.update {
                    it.copy(isLoading = false, movies = allMovies)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load movies")
                }
            }
        }
    }
}
