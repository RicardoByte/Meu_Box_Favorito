package com.boxel.meuboxfavorito.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.data.remote.Moviefmt
import com.boxel.meuboxfavorito.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Moviefmt>>(emptyList())
    val movies: StateFlow<List<Moviefmt>> = _movies.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    fun loadPopularMovies(apiKey: String) {
        viewModelScope.launch {
            if (apiKey.isBlank()) {
                _erro.value = "Chave da API do TMDb não encontrada. Configure TMDB_API_KEY no arquivo local.properties."
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