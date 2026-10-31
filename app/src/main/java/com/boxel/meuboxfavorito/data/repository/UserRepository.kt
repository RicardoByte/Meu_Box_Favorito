package com.boxel.meuboxfavorito.data.repository

import com.boxel.meuboxfavorito.data.local.LocalStorageManager
import com.boxel.meuboxfavorito.data.validator.AuthValidator
import com.boxel.meuboxfavorito.models.LoginResult
import com.boxel.meuboxfavorito.models.UserAccount
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Repositório central para dados de usuários, autenticação e sessão no aplicativo.
 */
object UserRepository {

    val currentUser: StateFlow<UserAccount?> = LocalStorageManager.currentUserFlow
    val allUsers: StateFlow<List<UserAccount>> = LocalStorageManager.usersFlow

    /**
     * Autentica o usuário com base no e-mail ou nome de usuário e senha.
     */
    fun login(identificador: String, senha: String): LoginResult {
        val resultado = AuthValidator.autenticar(
            identificador = identificador,
            senha = senha,
            usuariosCadastrados = allUsers.value
        )
        if (resultado is LoginResult.Success) {
            LocalStorageManager.setActiveUser(resultado.user)
        }
        return resultado
    }

    /**
     * Cadastra um novo usuário persistindo todos os inputs fornecidos.
     */
    fun register(
        nomeCompleto: String,
        nomeUsuario: String,
        email: String,
        senha: String,
        generosFavoritos: Set<String>
    ): Result<UserAccount> {
        val cleanUser = nomeUsuario.trim().removePrefix("@")
        val cleanEmail = email.trim().lowercase()

        // Verifica duplicidade de usuário
        val userExistente = allUsers.value.any {
            it.nomeUsuario.equals(cleanUser, ignoreCase = true)
        }
        if (userExistente) {
            return Result.failure(IllegalArgumentException("Este nome de usuário já está em uso."))
        }

        // Verifica duplicidade de e-mail
        val emailExistente = allUsers.value.any {
            it.email.equals(cleanEmail, ignoreCase = true)
        }
        if (emailExistente) {
            return Result.failure(IllegalArgumentException("Este e-mail já está cadastrado."))
        }

        val novoUsuario = UserAccount(
            id = UUID.randomUUID().toString(),
            nomeCompleto = nomeCompleto.trim(),
            nomeUsuario = cleanUser,
            email = cleanEmail,
            senhaHash = senha.trim(),
            generosFavoritos = generosFavoritos,
            bio = "Novo curador no Meu Box explorando universos cinematográficos.",
            avatarUrl = null,
            joinYear = 2026,
            seguidores = 0
        )

        LocalStorageManager.saveUser(novoUsuario)
        LocalStorageManager.setActiveUser(novoUsuario)
        return Result.success(novoUsuario)
    }

    /**
     * Atualiza o perfil do usuário logado (nome, bio e avatar) e propaga reativamente.
     */
    fun updateProfile(nomeCompleto: String, bio: String, avatarUrl: String?) {
        LocalStorageManager.updateProfile(nomeCompleto, bio, avatarUrl)
    }

    /**
     * Desloga da conta atual.
     */
    fun logout() {
        LocalStorageManager.logout()
    }

    /**
     * Retorna o usuário logado atual.
     */
    fun getCurrentUser(): UserAccount? = LocalStorageManager.getActiveUser()
}
