package com.boxel.meuboxfavorito.models

import kotlinx.serialization.Serializable

/**
 * Nível de segurança da senha para exibição na barra de força.
 */
enum class ForcaSenha(val nivel: Int, val rotulo: String) {
    VAZIA(0, ""),
    FRACA(1, "Fraca"),
    MEDIA(2, "Média"),
    FORTE(3, "Forte"),
    MUITO_FORTE(4, "Muito forte")
}

/**
 * Estado completo do formulário de criação de conta.
 */
data class RegisterFormState(
    val nomeCompleto: String = "",
    val nomeUsuario: String = "",
    val email: String = "",
    val senha: String = "",
    val confirmarSenha: String = "",
    val generosFavoritos: Set<String> = emptySet(),
    val termosAceitos: Boolean = false,
    val senhaVisivel: Boolean = false,

    val nomeCompletoErro: String? = null,
    val nomeUsuarioErro: String? = null,
    val emailErro: String? = null,
    val senhaErro: String? = null,
    val confirmarSenhaErro: String? = null,
    val termosErro: String? = null,

    val forca: ForcaSenha = ForcaSenha.VAZIA,
    val submetido: Boolean = false
) {
    val isValido: Boolean
        get() = nomeCompletoErro == null
                && nomeUsuarioErro == null
                && emailErro == null
                && senhaErro == null
                && confirmarSenhaErro == null
                && termosErro == null
}

/**
 * Resultado da autenticação de login do usuário.
 */
sealed class LoginResult {
    data class Success(val user: UserAccount) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
