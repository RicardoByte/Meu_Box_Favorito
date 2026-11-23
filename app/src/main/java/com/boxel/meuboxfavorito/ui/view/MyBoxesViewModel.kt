package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.MovieRepository
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.BoxPrivacyType
import com.boxel.meuboxfavorito.models.Moviefmt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyBoxesViewModel(
    private val repository: MovieRepository = MovieRepository(),
    private val boxRepository: BoxDetailRepository = BoxDetailRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Moviefmt>>(emptyList())
    val movies: StateFlow<List<Moviefmt>> = _movies.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _filtroSelecionado = MutableStateFlow("Todas")
    val filtroSelecionado: StateFlow<String> = _filtroSelecionado.asStateFlow()

    val boxes: StateFlow<List<BoxDetailData>> = boxRepository.boxesFlow

    val boxesFiltradas: StateFlow<List<BoxDetailData>> = combine(
        boxes,
        _filtroSelecionado,
        _searchText
    ) { allBoxes, filtro, query ->
        allBoxes.filter { box ->
            val passaFiltro = when (filtro) {
                "Criadas por mim" -> {
                    box.privacyType == BoxPrivacyType.PRIVADA || box.privacyType == BoxPrivacyType.PUBLICA
                }
                "Colaborativas" -> {
                    box.privacyType == BoxPrivacyType.COLABORATIVA
                }
                else -> true
            }

            val passaBusca = query.isBlank() || box.title.contains(query, ignoreCase = true)
            passaFiltro && passaBusca
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchChange(text: String) {
        _searchText.value = text
    }

    fun onFiltroChange(filtro: String) {
        _filtroSelecionado.value = filtro
    }

    fun loadMovies(apiKey: String) {
        viewModelScope.launch {
            if (apiKey.isBlank()) {
                _erro.value = "Chave da API do TMDb não encontrada."
                return@launch
            }
            _carregando.value = true
            _erro.value = null
            try {
                _movies.value = repository.getPopularMovies(apiKey)
            } catch (e: Exception) {
                _erro.value = "Erro ao buscar filmes: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }
}
