package com.boxel.meuboxfavorito.models

import kotlinx.serialization.Serializable

@Serializable
enum class BoxPrivacyType(val label: String) {
    PUBLICA("Pública"),
    PRIVADA("Privada"),
    COLABORATIVA("Colaborativa")
}

@Serializable
enum class TagStyle {
    HIGHLIGHT,   // Purple (DESTAQUE)
    TOP,         // Teal (Top Resenha)
    VIRAL,       // Orange / Red (VIRAL)
    OPEN,        // Slate (Discussão Aberta)
    PRIVATE,     // Amber / Gold (NOTA PRIVADA / DIÁRIO)
    COLLABORATOR // Blue / Indigo (COLABORADOR)
}

@Serializable
data class RespostaComunidade(
    val id: Long,
    val autor: String,
    val tempoRelativo: String,
    val conteudo: String,
    val curtidas: Int,
    val avatarUrl: String? = null,
    val curtidoPorMim: Boolean = false
)

@Serializable
data class BoxReviewItem(
    val id: Int,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String? = null,
    val timeAgo: String,
    val tagText: String,
    val tagStyle: TagStyle,
    val movieTitle: String,
    val movieInfo: String,
    val moviePosterUrl: String? = null,
    val rating: Float,
    val title: String,
    val body: String,
    val hashtags: List<String> = emptyList(),
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val replies: List<RespostaComunidade> = emptyList()
)

@Serializable
data class BoxDetailData(
    val id: Int,
    val title: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String? = null,
    val badgeCuradoria: String,
    val updatedTime: String,
    val description: String,
    val filmesCount: Int,
    val viewsCount: String,
    val savesCount: String,
    val isSaved: Boolean = false,
    val privacyType: BoxPrivacyType,
    val coverUrl: String? = null,
    val filmes: List<Moviefmt> = emptyList(),
    val reviews: List<BoxReviewItem> = emptyList()
)

@Serializable
data class ReviewMovieSelection(
    val title: String,
    val director: String,
    val duration: String,
    val year: String,
    val posterUrl: String,
    val genres: List<String>
)
