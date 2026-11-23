package com.boxel.meuboxfavorito.data.validator

import com.boxel.meuboxfavorito.models.LoginResult
import com.boxel.meuboxfavorito.models.UserAccount

/**
 * Objeto responsável pela validação e autenticação das credenciais fornecidas pelo usuário.
 */
object AuthValidator {

    /**
     * Valida os campos de entrada de login e autentica contra a base de usuários persistidos.
     */
    fun autenticar(
        identificador: String,
        senha: String,
        usuariosCadastrados: List<UserAccount>
    ): LoginResult {
        val idTrim = identificador.trim()
        val senhaTrim = senha.trim()

        if (idTrim.isBlank()) {
            return LoginResult.Error("Informe seu e-mail ou nome de usuário")
        }

        if (senhaTrim.isBlank()) {
            return LoginResult.Error("Informe sua senha de acesso")
        }

        if (senhaTrim.length < 6) {
            return LoginResult.Error("A senha deve ter no mínimo 6 caracteres")
        }

        // Procura usuário por e-mail ou nome de usuário (case-insensitive)
        val usuario = usuariosCadastrados.firstOrNull {
            it.email.equals(idTrim, ignoreCase = true) ||
            it.nomeUsuario.equals(idTrim.removePrefix("@"), ignoreCase = true)
        } ?: return LoginResult.Error("Nenhuma conta encontrada com este e-mail/usuário")

        // Valida senha
        if (usuario.senhaHash != senhaTrim) {
            return LoginResult.Error("Senha incorreta. Tente novamente.")
        }

        return LoginResult.Success(usuario)
    }

    /**
     * Validação simples de formato de identificador.
     */
    fun validarIdentificador(identificador: String): String? {
        if (identificador.isBlank()) return "Campo obrigatório"
        return null
    }

    /**
     * Validação simples de formato de senha.
     */
    fun validarSenha(senha: String): String? {
        if (senha.isBlank()) return "Senha obrigatória"
        if (senha.length < 6) return "Mínimo 6 caracteres"
        return null
    }
}
