package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.MovieRepository
import com.boxel.meuboxfavorito.data.validator.BoxValidator
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.BoxPrivacyType
import com.boxel.meuboxfavorito.models.Moviefmt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateBoxUiState(
    val titulo: String = "",
    val descricao: String = "",
    val privacidade: BoxPrivacyType = BoxPrivacyType.PUBLICA,
    val colaborativa: Boolean = false,
    val erroTitulo: Boolean = false,

    val capaSelecionadaUrl: String? = null,
    val filmeCapaNome: String? = null,
    val buscaCapa: String = "",
    val mostrandoBuscaCapa: Boolean = false,
    val resultadosCapa: List<Moviefmt> = emptyList(),
    val buscandoCapa: Boolean = false,

    val buscaFilme: String = "",
    val filmesAdicionados: List<Moviefmt> = emptyList(),
    val resultadosFilmesBusca: List<Moviefmt> = emptyList(),
    val buscandoFilmes: Boolean = false
)

class CreateBoxViewModel(
    private val movieRepository: MovieRepository = MovieRepository(),
    private val boxRepository: BoxDetailRepository = BoxDetailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBoxUiState())
    val uiState: StateFlow<CreateBoxUiState> = _uiState.asStateFlow()

    private val _allMovies = MutableStateFlow<List<Moviefmt>>(emptyList())
    val allMovies: StateFlow<List<Moviefmt>> = _allMovies.asStateFlow()

    private var searchCapaJob: Job? = null
    private var searchFilmeJob: Job? = null

    init {
        loadPopularMovies()
    }

    private fun loadPopularMovies() {
        viewModelScope.launch {
            try {
                if (BuildConfig.TMDB_API_KEY.isNotBlank()) {
                    _allMovies.value = movieRepository.getPopularMovies(BuildConfig.TMDB_API_KEY)
                }
            } catch (e: Exception) {
                // Falha silenciosa em caso de offline
            }
        }
    }

    fun onTituloChange(valor: String) {
        _uiState.update { it.copy(titulo = valor, erroTitulo = false) }
    }

    fun onDescricaoChange(valor: String) {
        _uiState.update { it.copy(descricao = valor) }
    }

    fun onPrivacidadeChange(tipo: BoxPrivacyType) {
        _uiState.update { it.copy(privacidade = tipo) }
    }

    fun onColaborativaChange(valor: Boolean) {
        _uiState.update { it.copy(colaborativa = valor) }
    }

    fun toggleMostrandoBuscaCapa() {
        _uiState.update { it.copy(mostrandoBuscaCapa = !it.mostrandoBuscaCapa) }
    }

    fun onBuscaCapaChange(query: String) {
        _uiState.update { it.copy(buscaCapa = query) }
        searchCapaJob?.cancel()
        if (query.length >= 2) {
            searchCapaJob = viewModelScope.launch {
                _uiState.update { it.copy(buscandoCapa = true) }
                delay(350)
                try {
                    val res = movieRepository.searchMovies(BuildConfig.TMDB_API_KEY, query)
                    val filtrados = res.filter { !it.backdrop_path.isNullOrBlank() || !it.poster_path.isNullOrBlank() }
                    _uiState.update { it.copy(resultadosCapa = filtrados, buscandoCapa = false) }
                } catch (e: Exception) {
                    val fallback = _allMovies.value.filter { it.title.contains(query, ignoreCase = true) }
                    _uiState.update { it.copy(resultadosCapa = fallback, buscandoCapa = false) }
                }
            }
        } else {
            _uiState.update { it.copy(resultadosCapa = emptyList(), buscandoCapa = false) }
        }
    }

    fun onSelectCapa(url: String, filmeTitulo: String) {
        _uiState.update {
            it.copy(
                capaSelecionadaUrl = url,
                filmeCapaNome = filmeTitulo,
                mostrandoBuscaCapa = false,
                buscaCapa = ""
            )
        }
    }

    fun onClearCapa() {
        _uiState.update { it.copy(capaSelecionadaUrl = null, filmeCapaNome = null) }
    }

    fun onBuscaFilmeChange(query: String) {
        _uiState.update { it.copy(buscaFilme = query) }
        searchFilmeJob?.cancel()
        if (query.length >= 2) {
            searchFilmeJob = viewModelScope.launch {
                _uiState.update { it.copy(buscandoFilmes = true) }
                delay(350)
                try {
                    val res = movieRepository.searchMovies(BuildConfig.TMDB_API_KEY, query)
                    val adicionados = _uiState.value.filmesAdicionados
                    val filtrados = res.filter { m -> adicionados.none { it.id == m.id } }
                    _uiState.update { it.copy(resultadosFilmesBusca = filtrados, buscandoFilmes = false) }
                } catch (e: Exception) {
                    val adicionados = _uiState.value.filmesAdicionados
                    val fallback = _allMovies.value
                        .filter { it.title.contains(query, ignoreCase = true) }
                        .filter { m -> adicionados.none { it.id == m.id } }
                    _uiState.update { it.copy(resultadosFilmesBusca = fallback, buscandoFilmes = false) }
                }
            }
        } else {
            _uiState.update { it.copy(resultadosFilmesBusca = emptyList(), buscandoFilmes = false) }
        }
    }

    fun onAddFilme(filme: Moviefmt) {
        _uiState.update {
            it.copy(
                filmesAdicionados = it.filmesAdicionados + filme,
                buscaFilme = "",
                resultadosFilmesBusca = emptyList()
            )
        }
    }

    fun onRemoveFilme(filme: Moviefmt) {
        _uiState.update {
            it.copy(filmesAdicionados = it.filmesAdicionados.filter { f -> f.id != filme.id })
        }
    }

    fun createBox(): BoxDetailData? {
        val state = _uiState.value
        val validacao = BoxValidator.validarTitulo(state.titulo)
        if (validacao != null) {
            _uiState.update { it.copy(erroTitulo = true) }
            return null
        }

        return boxRepository.createBox(
            title = state.titulo,
            description = state.descricao,
            privacyType = state.privacidade,
            coverUrl = state.capaSelecionadaUrl,
            filmes = state.filmesAdicionados
        )
    }
}
