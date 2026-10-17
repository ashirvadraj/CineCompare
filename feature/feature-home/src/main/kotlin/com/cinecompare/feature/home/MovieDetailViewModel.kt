package com.cinecompare.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinecompare.aggregator.base.models.Movie
import com.cinecompare.core.database.dao.MovieDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val isWatchlisted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieDao: MovieDao
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailUiState())
    val state: StateFlow<MovieDetailUiState> = _state.asStateFlow()

    fun loadMovie(movieId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val entity = movieDao.getMovieById(movieId)
            if (entity != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        movie = Movie(
                            id = entity.id,
                            title = entity.title,
                            posterUrl = entity.posterUrl,
                            bannerUrl = entity.bannerUrl,
                            synopsis = entity.synopsis,
                            certification = entity.certification,
                            durationMinutes = entity.durationMinutes,
                            rating = entity.rating
                        ),
                        isWatchlisted = entity.isWatchlisted
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleWatchlist(movieId: String) {
        viewModelScope.launch {
            val current = _state.value.isWatchlisted
            movieDao.setWatchlisted(movieId, !current)
            _state.update { it.copy(isWatchlisted = !current) }
        }
    }
}
