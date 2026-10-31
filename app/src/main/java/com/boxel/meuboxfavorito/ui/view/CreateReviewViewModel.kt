package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.MovieRepository
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.Moviefmt
import com.boxel.meuboxfavorito.models.ReviewMovieSelection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateReviewUiState(
    val allBoxes: List<BoxDetailData> = emptyList(),
    val selectedBox: BoxDetailData? = null,
    val selectedMovie: ReviewMovieSelection = ReviewMovieSelection(
        title = "Seven: Os Sete Crimes Capitais",
        director = "David Fincher",
        duration = "2h 07m",
        year = "1995",
        posterUrl = "https://image.tmdb.org/t/p/w500/6yoghtyTpznpBik8EngEmJskVUO.jpg",
        genres = listOf("Crime", "Mistério", "Thriller")
    ),
    val rating: Float = 4.5f,
    val debateTitle: String = "",
    val analysisText: String = "",
    val quoteText: String = "",
    val currentTagInput: String = "",
    val hashtags: List<String> = listOf("Cinematografia", "Roteiro"),
    val isObraPrima: Boolean = false,
    val isFavorito: Boolean = false,
    val hasSpoilers: Boolean = false,
    val showMovieSearchDialog: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Moviefmt> = emptyList(),
    val isSearching: Boolean = false
)

class CreateReviewViewModel(
    private val boxRepository: BoxDetailRepository = BoxDetailRepository,
    private val movieRepository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReviewUiState())
    val uiState: StateFlow<CreateReviewUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun initialize(initialBoxId: Int) {
        val boxes = boxRepository.getAllBoxes()
        val box = boxRepository.getBoxById(initialBoxId)
        val firstInBox = box.filmes.firstOrNull()

        val defaultMovie = if (firstInBox != null) {
            ReviewMovieSelection(
                title = firstInBox.title,
                director = "Diretor",
                duration = "TMDB",
                year = firstInBox.release_date?.take(4) ?: "2024",
                posterUrl = firstInBox.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
                genres = listOf("Cinema")
            )
        } else {
            _uiState.value.selectedMovie
        }

        _uiState.update {
            it.copy(
                allBoxes = boxes,
                selectedBox = box,
                selectedMovie = defaultMovie
            )
        }
    }

    fun onSelectBox(box: BoxDetailData) {
        _uiState.update { it.copy(selectedBox = box) }
    }

    fun onSelectMovie(movie: ReviewMovieSelection) {
        _uiState.update { it.copy(selectedMovie = movie, showMovieSearchDialog = false) }
    }

    fun onRatingChange(rating: Float) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun onDebateTitleChange(title: String) {
        _uiState.update { it.copy(debateTitle = title) }
    }

    fun onAnalysisTextChange(text: String) {
        _uiState.update { it.copy(analysisText = text) }
    }

    fun onQuoteTextChange(quote: String) {
        _uiState.update { it.copy(quoteText = quote) }
    }

    fun onTagInputChange(tag: String) {
        _uiState.update { it.copy(currentTagInput = tag) }
    }

    fun addTag(tag: String) {
        val limpo = tag.trim().removePrefix("#")
        if (limpo.isNotBlank() && !_uiState.value.hashtags.contains(limpo)) {
            _uiState.update {
                it.copy(
                    hashtags = it.hashtags + limpo,
                    currentTagInput = ""
                )
            }
        }
    }

    fun removeTag(tag: String) {
        _uiState.update {
            it.copy(hashtags = it.hashtags.filter { t -> t != tag })
        }
    }

    fun toggleObraPrima() {
        _uiState.update { it.copy(isObraPrima = !it.isObraPrima) }
    }

    fun toggleFavorito() {
        _uiState.update { it.copy(isFavorito = !it.isFavorito) }
    }

    fun toggleSpoilers() {
        _uiState.update { it.copy(hasSpoilers = !it.hasSpoilers) }
    }

    fun setShowMovieSearchDialog(show: Boolean) {
        _uiState.update { it.copy(showMovieSearchDialog = show) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                _uiState.update { it.copy(isSearching = true) }
                delay(350)
                try {
                    val results = movieRepository.searchMovies(BuildConfig.TMDB_API_KEY, query)
                    _uiState.update { it.copy(searchResults = results, isSearching = false) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                }
            }
        } else {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
        }
    }

    fun publishReview(): Int? {
        val state = _uiState.value
        val box = state.selectedBox ?: return null
        if (state.debateTitle.isBlank() || state.analysisText.isBlank()) return null

        boxRepository.addReview(
            boxId = box.id,
            movieTitle = state.selectedMovie.title,
            movieInfo = "${state.selectedMovie.year} • ${state.selectedMovie.genres.joinToString(" / ")}",
            moviePosterUrl = state.selectedMovie.posterUrl,
            rating = state.rating,
            title = state.debateTitle.trim(),
            body = state.analysisText.trim(),
            hashtags = state.hashtags,
            isObraPrima = state.isObraPrima,
            isFavorito = state.isFavorito,
            hasSpoilers = state.hasSpoilers
        )

        return box.id
    }
}
