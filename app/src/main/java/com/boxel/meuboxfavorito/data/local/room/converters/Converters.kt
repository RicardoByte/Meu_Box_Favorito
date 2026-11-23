package com.boxel.meuboxfavorito.data.local.room.converters

import androidx.room.TypeConverter
import com.boxel.meuboxfavorito.models.BoxPrivacyType
import com.boxel.meuboxfavorito.models.BoxReviewItem
import com.boxel.meuboxfavorito.models.Moviefmt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * TypeConverters para o Room Database.
 * Converte tipos complexos que o Room não suporta nativamente
 * para tipos primitivos armazenáveis (String) e vice-versa.
 */
class Converters {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    // ─── BoxPrivacyType ───────────────────────────────────────────────────

    @TypeConverter
    fun privacyTypeToString(privacyType: BoxPrivacyType): String = privacyType.name

    @TypeConverter
    fun stringToPrivacyType(value: String): BoxPrivacyType =
        BoxPrivacyType.valueOf(value)

    // ─── List<String> ────────────────────────────────────────────────────

    @TypeConverter
    fun stringListToJson(list: List<String>): String =
        json.encodeToString(list)

    @TypeConverter
    fun jsonToStringList(value: String): List<String> =
        try { json.decodeFromString(value) } catch (e: Exception) { emptyList() }

    // ─── Set<String> ─────────────────────────────────────────────────────

    @TypeConverter
    fun stringSetToJson(set: Set<String>): String =
        json.encodeToString(set.toList())

    @TypeConverter
    fun jsonToStringSet(value: String): Set<String> =
        try { json.decodeFromString<List<String>>(value).toSet() } catch (e: Exception) { emptySet() }

    // ─── List<Moviefmt> ──────────────────────────────────────────────────

    @TypeConverter
    fun movieListToJson(filmes: List<Moviefmt>): String =
        json.encodeToString(filmes)

    @TypeConverter
    fun jsonToMovieList(value: String): List<Moviefmt> =
        try { json.decodeFromString(value) } catch (e: Exception) { emptyList() }

    // ─── List<BoxReviewItem> ─────────────────────────────────────────────

    @TypeConverter
    fun reviewListToJson(reviews: List<BoxReviewItem>): String =
        json.encodeToString(reviews)

    @TypeConverter
    fun jsonToReviewList(value: String): List<BoxReviewItem> =
        try { json.decodeFromString(value) } catch (e: Exception) { emptyList() }
}
