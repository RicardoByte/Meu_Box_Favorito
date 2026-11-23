package com.boxel.meuboxfavorito.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.BoxPrivacyType
import com.boxel.meuboxfavorito.models.BoxReviewItem
import com.boxel.meuboxfavorito.models.Moviefmt

/**
 * Entidade Room que representa um Box no cache local.
 *
 * Uso: cache de boxes para usuários sem conta (modo convidado / guest).
 * Tipos complexos são serializados via [Converters].
 */
@Entity(tableName = "boxes")
data class BoxEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String?,
    val badgeCuradoria: String,
    val updatedTime: String,
    val description: String,
    val filmesCount: Int,
    val viewsCount: String,
    val savesCount: String,
    val isSaved: Boolean,
    val privacyType: BoxPrivacyType,
    val coverUrl: String?,
    // Serialized via TypeConverter
    val filmes: List<Moviefmt>,
    val reviews: List<BoxReviewItem>,
    // Identificador do owner — null = guest / anônimo
    val ownerUserId: String? = null
)

// ─── Mapeamentos de domínio ───────────────────────────────────────────────────

fun BoxDetailData.toEntity(ownerUserId: String? = null) = BoxEntity(
    id = id,
    title = title,
    creatorName = creatorName,
    creatorHandle = creatorHandle,
    creatorAvatar = creatorAvatar,
    badgeCuradoria = badgeCuradoria,
    updatedTime = updatedTime,
    description = description,
    filmesCount = filmesCount,
    viewsCount = viewsCount,
    savesCount = savesCount,
    isSaved = isSaved,
    privacyType = privacyType,
    coverUrl = coverUrl,
    filmes = filmes,
    reviews = reviews,
    ownerUserId = ownerUserId
)

fun BoxEntity.toDomain() = BoxDetailData(
    id = id,
    title = title,
    creatorName = creatorName,
    creatorHandle = creatorHandle,
    creatorAvatar = creatorAvatar,
    badgeCuradoria = badgeCuradoria,
    updatedTime = updatedTime,
    description = description,
    filmesCount = filmesCount,
    viewsCount = viewsCount,
    savesCount = savesCount,
    isSaved = isSaved,
    privacyType = privacyType,
    coverUrl = coverUrl,
    filmes = filmes,
    reviews = reviews
)
