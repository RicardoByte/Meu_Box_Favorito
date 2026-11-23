package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.MovieRepository
import com.boxel.meuboxfavorito.data.repository.UserRepository
import com.boxel.meuboxfavorito.data.validator.ReviewValidator
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

// ─── Form state com erros inline ──────────────────────────────────────────────

data class CreateReviewFormErrors(
    val titulo: String? = null,
    val analise: String? = null,
    val nota: String? = null,
    val filme: String? = null,
    val permissao: String? = null
) {
    val temErros: Boolean
        get() = titulo != null || analise != null || nota != null || filme != null || permissao != null
}

// ─── UI State ─────────────────────────────────────────────────────────────────

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
    val isSearching: Boolean = false,

    // ── Validação ──
    val formErrors: CreateReviewFormErrors = CreateReviewFormErrors(),
    /** true após a primeira tentativa de publicar — ativa revalidação em tempo real */
    val submetido: Boolean = false,
    /** null = sem erro de permissão bloqueante ainda verificado */
    val permissaoNegada: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

class CreateReviewViewModel(
    private val boxRepository: BoxDetailRepository = BoxDetailRepository,
    private val movieRepository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReviewUiState())
    val uiState: StateFlow<CreateReviewUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    // ─── Inicialização ────────────────────────────────────────────────────

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

        // Verificação inicial de permissão
        val usuario = UserRepository.getCurrentUser()
        val erroPermissao = ReviewValidator.verificarPermissao(usuario, box)

        _uiState.update {
            it.copy(
                allBoxes = boxes,
                selectedBox = box,
                selectedMovie = defaultMovie,
                permissaoNegada = erroPermissao
            )
        }
    }

    // ─── Eventos de campo ─────────────────────────────────────────────────

    fun onSelectBox(box: BoxDetailData) {
        val usuario = UserRepository.getCurrentUser()
        val erroPermissao = ReviewValidator.verificarPermissao(usuario, box)
        _uiState.update {
            it.copy(
                selectedBox = box,
                permissaoNegada = erroPermissao,
                // Revalida permissão no formErrors se já submetido
                formErrors = if (it.submetido)
                    it.formErrors.copy(permissao = erroPermissao)
                else it.formErrors
            )
        }
    }

    fun onSelectMovie(movie: ReviewMovieSelection) {
        _uiState.update {
            val erroFilme = if (it.submetido)
                ReviewValidator.validarFilmeSelecionado(movie.title) else it.formErrors.filme
            it.copy(
                selectedMovie = movie,
                showMovieSearchDialog = false,
                formErrors = it.formErrors.copy(filme = erroFilme)
            )
        }
    }

    fun onRatingChange(rating: Float) {
        _uiState.update {
            val erroNota = if (it.submetido) ReviewValidator.validarNota(rating) else it.formErrors.nota
            it.copy(rating = rating, formErrors = it.formErrors.copy(nota = erroNota))
        }
    }

    fun onDebateTitleChange(title: String) {
        _uiState.update {
            val erroTitulo = if (it.submetido) ReviewValidator.validarTitulo(title) else it.formErrors.titulo
            it.copy(debateTitle = title, formErrors = it.formErrors.copy(titulo = erroTitulo))
        }
    }

    fun onAnalysisTextChange(text: String) {
        _uiState.update {
            val erroAnalise = if (it.submetido) ReviewValidator.validarAnalise(text) else it.formErrors.analise
            it.copy(analysisText = text, formErrors = it.formErrors.copy(analise = erroAnalise))
        }
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
                it.copy(hashtags = it.hashtags + limpo, currentTagInput = "")
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

    // ─── Busca de filmes ──────────────────────────────────────────────────

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

    // ─── Publicação com validação completa ───────────────────────────────

    /**
     * Tenta publicar a resenha.
     *
     * 1. Verifica permissão do usuário na box selecionada.
     * 2. Valida todos os campos do formulário.
     * 3. Se tudo OK: persiste e retorna o boxId.
     * 4. Se falhar: atualiza [CreateReviewUiState.formErrors] e retorna null.
     */
    fun publishReview(): Int? {
        val state = _uiState.value
        val box = state.selectedBox

        // ── 1. Validação completa ──
        val usuario = UserRepository.getCurrentUser()
        val erros = ReviewValidator.validarFormulario(
            titulo = state.debateTitle,
            analise = state.analysisText,
            nota = state.rating,
            filmeTitle = state.selectedMovie.title,
            usuario = usuario,
            box = box
        )

        val formErrors = CreateReviewFormErrors(
            titulo = erros["titulo"],
            analise = erros["analise"],
            nota = erros["nota"],
            filme = erros["filme"],
            permissao = erros["permissao"]
        )

        // ── 2. Atualiza estado com erros e marca como submetido ──
        _uiState.update {
            it.copy(
                formErrors = formErrors,
                submetido = true,
                permissaoNegada = erros["permissao"]
            )
        }

        if (formErrors.temErros || box == null) return null

        // ── 3. Persiste a resenha ──
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
