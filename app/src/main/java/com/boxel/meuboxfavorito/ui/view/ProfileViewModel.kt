package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.UserRepository
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val showEditDialog: Boolean = false
)

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository,
    private val boxRepository: BoxDetailRepository = BoxDetailRepository
) : ViewModel() {

    val currentUser: StateFlow<UserAccount?> = userRepository.currentUser

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val userBoxes: StateFlow<List<BoxDetailData>> = combine(
        boxRepository.boxesFlow,
        currentUser
    ) { allBoxes, user ->
        val userHandle = "@${user?.nomeUsuario ?: "ricardobyte"}"
        val userName = user?.nomeCompleto ?: "Ricardo Byte"

        allBoxes.filter {
            it.creatorHandle.equals(userHandle, ignoreCase = true) ||
            it.creatorHandle == "@ricardobyte" ||
            it.creatorName.equals(userName, ignoreCase = true) ||
            it.creatorName == "Você"
        }.reversed()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalFilmes: StateFlow<Int> = combine(userBoxes) { boxes ->
        boxes.first().sumOf { it.filmesCount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun setShowEditDialog(show: Boolean) {
        _uiState.update { it.copy(showEditDialog = show) }
    }

    fun updateProfile(nome: String, bio: String, avatarUrl: String?) {
        userRepository.updateProfile(nome, bio, avatarUrl)
        setShowEditDialog(false)
    }

    fun logout() {
        userRepository.logout()
    }
}
