package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import com.boxel.meuboxfavorito.data.repository.UserRepository
import com.boxel.meuboxfavorito.models.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val emailOuUsuario: String = "",
    val senha: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailOuUsuarioChange(valor: String) {
        _uiState.update { it.copy(emailOuUsuario = valor, errorMessage = null) }
    }

    fun onSenhaChange(valor: String) {
        _uiState.update { it.copy(senha = valor, errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val resultado = userRepository.login(
            identificador = currentState.emailOuUsuario,
            senha = currentState.senha
        )

        when (resultado) {
            is LoginResult.Success -> {
                _uiState.update { it.copy(errorMessage = null) }
                onSuccess()
            }
            is LoginResult.Error -> {
                _uiState.update { it.copy(errorMessage = resultado.message) }
            }
        }
    }
}
