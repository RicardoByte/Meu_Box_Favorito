package com.boxel.meuboxfavorito.models

import kotlinx.serialization.Serializable

/**
 * Modelo de dados que representa a conta de um usuário persistida no sistema.
 * Armazena todos os inputs fornecidos durante o cadastro e edições de perfil.
 */
@Serializable
data class UserAccount(
    val id: String,
    val nomeCompleto: String,
    val nomeUsuario: String,
    val email: String,
    val senhaHash: String,
    val generosFavoritos: Set<String> = emptySet(),
    val bio: String = "Curando as melhores experiências cinematográficas no Meu Box.",
    val avatarUrl: String? = null,
    val joinYear: Int = 2026,
    val seguidores: Int = 0
)
