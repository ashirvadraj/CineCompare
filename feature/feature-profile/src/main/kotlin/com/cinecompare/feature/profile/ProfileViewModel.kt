package com.cinecompare.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinecompare.aggregator.base.models.Movie
import com.cinecompare.core.database.dao.MovieDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val watchlistMovies: List<Movie> = emptyList(),
    val priceAlertMovies: List<Movie> = emptyList()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val movieDao: MovieDao
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            movieDao.getWatchlistedMovies().collect { entities ->
                val movies = entities.map { entity ->
                    Movie(
                        id = entity.id,
                        title = entity.title,
                        posterUrl = entity.posterUrl,
                        rating = entity.rating
                    )
                }
                val alertMovies = movies.filter {
                    entities.firstOrNull { e -> e.id == it.id }?.hasPriceAlert == true
                }
                _state.update { it.copy(watchlistMovies = movies, priceAlertMovies = alertMovies) }
            }
        }
    }

    fun togglePriceAlert(movieId: String) {
        viewModelScope.launch {
            val movie = movieDao.getMovieById(movieId) ?: return@launch
            movieDao.setPriceAlert(movieId, !movie.hasPriceAlert)
        }
    }

    fun removePriceAlert(movieId: String) {
        viewModelScope.launch { movieDao.setPriceAlert(movieId, false) }
    }
}
