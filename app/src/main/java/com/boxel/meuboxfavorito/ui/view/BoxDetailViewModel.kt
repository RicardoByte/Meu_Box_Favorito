package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.UserRepository
import com.boxel.meuboxfavorito.data.validator.ReviewValidator
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BoxDetailUiState(
    val boxId: Int = 1,
    val boxData: BoxDetailData? = null,
    val isSaved: Boolean = false,
    val selectedFilter: String = "Mais Relevantes",
    val isLoading: Boolean = false
)

class BoxDetailViewModel(
    private val repository: BoxDetailRepository = BoxDetailRepository,
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    val currentUser: StateFlow<UserAccount?> = userRepository.currentUser

    private val _uiState = MutableStateFlow(BoxDetailUiState())
    val uiState: StateFlow<BoxDetailUiState> = _uiState.asStateFlow()

    fun loadBox(boxId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(boxId = boxId, isLoading = true) }
            val data = repository.getBoxById(boxId)
            _uiState.update {
                it.copy(
                    boxData = data,
                    isSaved = data.isSaved,
                    isLoading = false
                )
            }
        }
    }

    fun toggleSave() {
        val current = _uiState.value.boxData ?: return
        val newSaved = repository.toggleSaveBox(current.id)
        _uiState.update {
            it.copy(
                isSaved = newSaved,
                boxData = it.boxData?.copy(isSaved = newSaved)
            )
        }
    }

    fun toggleLikeReview(reviewId: Int) {
        val current = _uiState.value.boxData ?: return
        val updatedReview = repository.toggleLikeReview(current.id, reviewId)
        if (updatedReview != null) {
            val freshData = repository.getBoxById(current.id)
            _uiState.update { it.copy(boxData = freshData) }
        }
    }

    fun deleteBox(onDeleted: () -> Unit) {
        val current = _uiState.value.boxData ?: return
        val user = currentUser.value
        if (ReviewValidator.podeDeletarBox(user, current)) {
            repository.deleteBox(current.id)
            onDeleted()
        }
    }

    fun deleteReview(reviewId: Int) {
        val current = _uiState.value.boxData ?: return
        val review = current.reviews.firstOrNull { it.id == reviewId } ?: return
        val user = currentUser.value
        if (ReviewValidator.podeDeletarResenha(user, current, review.authorHandle, review.authorName)) {
            val sucesso = repository.deleteReview(current.id, reviewId)
            if (sucesso) {
                val fresh = repository.getBoxById(current.id)
                _uiState.update { it.copy(boxData = fresh) }
            }
        }
    }

    fun onFilterChange(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}
