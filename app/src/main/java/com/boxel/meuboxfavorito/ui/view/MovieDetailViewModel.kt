package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.repository.MovieRepository
import com.boxel.meuboxfavorito.models.Moviefmt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movieId: Int = 0,
    val movie: Moviefmt? = null,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MovieDetailViewModel(
    private val movieRepository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun loadMovie(movieId: Int) {
        _uiState.update { it.copy(movieId = movieId, isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                var found: Moviefmt? = null
                if (BuildConfig.TMDB_API_KEY.isNotBlank()) {
                    found = movieRepository.getMovieDetail(BuildConfig.TMDB_API_KEY, movieId)
                }
                if (found == null) {
                    val popular = movieRepository.getPopularMovies(BuildConfig.TMDB_API_KEY)
                    found = popular.firstOrNull { it.id == movieId }
                }

                _uiState.update {
                    it.copy(
                        movie = found,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun toggleSaved() {
        _uiState.update { it.copy(isSaved = !it.isSaved) }
    }
}
