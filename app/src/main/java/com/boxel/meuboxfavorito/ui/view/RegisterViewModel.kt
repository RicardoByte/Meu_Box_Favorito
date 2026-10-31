package com.boxel.meuboxfavorito.ui.view

import androidx.lifecycle.ViewModel
import com.boxel.meuboxfavorito.ui.screen.auth.ForcaSenha
import com.boxel.meuboxfavorito.ui.screen.auth.RegisterFormState
import com.boxel.meuboxfavorito.ui.screen.auth.RegisterValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel responsável por orquestrar o estado do formulário de criação de conta.
 *
 * Expõe [uiState] como StateFlow imutável para a UI.
 * Toda mutação passa por funções específicas que garantem revalidação
 * incremental (campo a campo) quando o formulário já foi submetido.
 *
 * Sem persistência — a lógica de persistência virá em implementação futura.
 */
class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterFormState())
    val uiState: StateFlow<RegisterFormState> = _uiState.asStateFlow()

    // ─── Atualizações de campo ────────────────────────────────────────────

    fun onNomeCompletoChange(valor: String) {
        _uiState.update { state ->
            state.copy(
                nomeCompleto = valor,
                // Revalida em tempo real após primeira submissão
                nomeCompletoErro = if (state.submetido)
                    RegisterValidator.validarNomeCompleto(valor) else state.nomeCompletoErro
            )
        }
    }

    fun onNomeUsuarioChange(valor: String) {
        _uiState.update { state ->
            state.copy(
                nomeUsuario = valor,
                nomeUsuarioErro = if (state.submetido)
                    RegisterValidator.validarNomeUsuario(valor) else state.nomeUsuarioErro
            )
        }
    }

    fun onEmailChange(valor: String) {
        _uiState.update { state ->
            state.copy(
                email = valor,
                emailErro = if (state.submetido)
                    RegisterValidator.validarEmail(valor) else state.emailErro
            )
        }
    }

    fun onSenhaChange(valor: String) {
        _uiState.update { state ->
            state.copy(
                senha = valor,
                forca = RegisterValidator.calcularForcaSenha(valor),
                senhaErro = if (state.submetido)
                    RegisterValidator.validarSenha(valor) else state.senhaErro,
                // Revalida confirmar senha se já foi digitada
                confirmarSenhaErro = if (state.submetido && state.confirmarSenha.isNotEmpty())
                    RegisterValidator.validarConfirmarSenha(valor, state.confirmarSenha)
                else state.confirmarSenhaErro
            )
        }
    }

    fun onConfirmarSenhaChange(valor: String) {
        _uiState.update { state ->
            state.copy(
                confirmarSenha = valor,
                confirmarSenhaErro = if (state.submetido)
                    RegisterValidator.validarConfirmarSenha(state.senha, valor)
                else state.confirmarSenhaErro
            )
        }
    }

    fun onToggleGenero(genero: String) {
        _uiState.update { state ->
            val atualizados = state.generosFavoritos.toMutableSet().apply {
                if (contains(genero)) remove(genero) else add(genero)
            }
            state.copy(generosFavoritos = atualizados)
        }
    }

    fun onTermosChange(aceito: Boolean) {
        _uiState.update { state ->
            state.copy(
                termosAceitos = aceito,
                termosErro = if (state.submetido)
                    RegisterValidator.validarTermos(aceito) else state.termosErro
            )
        }
    }

    fun onToggleSenhaVisivel() {
        _uiState.update { it.copy(senhaVisivel = !it.senhaVisivel) }
    }

    // ─── Submissão do formulário ──────────────────────────────────────────

    fun onSubmit(): Boolean {
        val estadoValidado = RegisterValidator.validarFormulario(_uiState.value)
        if (!estadoValidado.isValido) {
            _uiState.update { estadoValidado }
            return false
        }

        val result = com.boxel.meuboxfavorito.data.repository.UserRepository.register(
            nomeCompleto = estadoValidado.nomeCompleto,
            nomeUsuario = estadoValidado.nomeUsuario,
            email = estadoValidado.email,
            senha = estadoValidado.senha,
            generosFavoritos = estadoValidado.generosFavoritos
        )

        return if (result.isSuccess) {
            _uiState.update { estadoValidado }
            true
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Erro ao cadastrar usuário"
            _uiState.update { state ->
                if (errorMsg.contains("usuário", ignoreCase = true)) {
                    state.copy(nomeUsuarioErro = errorMsg)
                } else {
                    state.copy(emailErro = errorMsg)
                }
            }
            false
        }
    }
}
