package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.BoxReviewItem
import com.boxel.meuboxfavorito.models.RespostaComunidade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiscussionUiState(
    val boxId: Int = 1,
    val reviewId: Int = 101,
    val boxData: BoxDetailData? = null,
    val review: BoxReviewItem? = null,
    val comentarios: List<RespostaComunidade> = emptyList(),
    val novoComentarioTexto: String = "",
    val curtido: Boolean = false,
    val curtidasCount: Int = 0,
    val isLoading: Boolean = false
)

class DiscussionViewModel(
    private val repository: BoxDetailRepository = BoxDetailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionUiState())
    val uiState: StateFlow<DiscussionUiState> = _uiState.asStateFlow()

    fun loadDiscussion(boxId: Int, reviewId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, boxId = boxId, reviewId = reviewId) }
            val box = repository.getBoxById(boxId)
            val rev = repository.getReviewById(boxId, reviewId) ?: box.reviews.firstOrNull()

            _uiState.update {
                it.copy(
                    boxData = box,
                    review = rev,
                    comentarios = rev?.replies ?: emptyList(),
                    curtido = rev?.isLiked ?: false,
                    curtidasCount = rev?.likesCount ?: 0,
                    isLoading = false
                )
            }
        }
    }

    fun onNovoComentarioChange(texto: String) {
        _uiState.update { it.copy(novoComentarioTexto = texto) }
    }

    fun toggleLike() {
        val currentReview = _uiState.value.review ?: return
        val boxId = _uiState.value.boxId
        val novoCurtido = !_uiState.value.curtido
        val novoTotal = if (novoCurtido) _uiState.value.curtidasCount + 1 else _uiState.value.curtidasCount - 1

        _uiState.update {
            it.copy(
                curtido = novoCurtido,
                curtidasCount = novoTotal
            )
        }

        repository.toggleLikeReview(boxId, currentReview.id)
    }

    fun sendComment() {
        val state = _uiState.value
        val texto = state.novoComentarioTexto.trim()
        val review = state.review ?: return

        if (texto.isBlank()) return

        val novo = repository.addCommentToReview(
            boxId = state.boxId,
            reviewId = review.id,
            content = texto
        )

        if (novo != null) {
            _uiState.update {
                it.copy(
                    comentarios = listOf(novo) + it.comentarios,
                    novoComentarioTexto = ""
                )
            }
        }
    }
}
