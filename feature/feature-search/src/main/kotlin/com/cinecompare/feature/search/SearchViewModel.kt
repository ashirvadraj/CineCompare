package com.cinecompare.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.aggregator.base.models.Movie
import com.cinecompare.aggregator.base.models.POPULAR_CITIES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val aggregators: Set<@JvmSuppressWildcards MovieAggregator>
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()
    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.length < 2) {
            _state.update { it.copy(results = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(400) // Debounce 400ms
            _state.update { it.copy(isLoading = true) }
            try {
                val city = POPULAR_CITIES.first()
                val allMovies = aggregators.map { agg ->
                    async {
                        try { agg.getNowShowing(city).getOrNull() ?: emptyList() }
                        catch (e: Exception) { emptyList() }
                    }
                }.awaitAll().flatten()

                val filtered = allMovies
                    .distinctBy { it.title }
                    .filter { it.title.contains(query, ignoreCase = true) }

                _state.update { it.copy(isLoading = false, results = filtered) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearSearch() = _state.update { it.copy(query = "", results = emptyList()) }
}
