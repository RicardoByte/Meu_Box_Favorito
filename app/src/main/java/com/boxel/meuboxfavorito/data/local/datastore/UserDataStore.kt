package com.boxel.meuboxfavorito.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.boxel.meuboxfavorito.models.UserAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensão de contexto — cria um único DataStore por processo
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_profile_datastore"
)

/**
 * DataStore de Preferences para persistência de dados de usuários cadastrados.
 *
 * Responsabilidade: armazenar de forma segura e reativa o perfil do usuário
 * autenticado — nome, bio, avatar, banner, gêneros favoritos e sessão ativa.
 *
 * Uso: somente para usuários com conta registrada.
 * Para cache de boxes de convidados, use [AppDatabase] + [BoxDao].
 */
class UserDataStore(private val context: Context) {

    companion object {
        // ─── Chaves de preferências (convenção: KEY_ prefix) ──────────────
        private val KEY_USER_ID             = stringPreferencesKey("key_user_id")
        private val KEY_NOME_COMPLETO       = stringPreferencesKey("key_nome_completo")
        private val KEY_NOME_USUARIO        = stringPreferencesKey("key_nome_usuario")
        private val KEY_EMAIL               = stringPreferencesKey("key_email")
        private val KEY_SENHA_HASH          = stringPreferencesKey("key_senha_hash")
        private val KEY_BIO                 = stringPreferencesKey("key_bio")
        private val KEY_AVATAR_URL          = stringPreferencesKey("key_avatar_url")
        private val KEY_BANNER_URL          = stringPreferencesKey("key_banner_url")
        private val KEY_JOIN_YEAR           = intPreferencesKey("key_join_year")
        private val KEY_SEGUIDORES          = intPreferencesKey("key_seguidores")
        private val KEY_GENEROS_FAVORITOS   = stringSetPreferencesKey("key_generos_favoritos")
        private val KEY_ACTIVE_USER_ID      = stringPreferencesKey("key_active_user_id")
    }

    // ─── Flow reativo do usuário autenticado ──────────────────────────────

    /**
     * Flow do usuário ativo. Emite null quando não há sessão ativa.
     * Emite [UserAccount] atualizado sempre que qualquer campo de perfil mudar.
     */
    val currentUserFlow: Flow<UserAccount?> = context.userDataStore.data.map { prefs ->
        val activeId = prefs[KEY_ACTIVE_USER_ID] ?: return@map null
        val userId   = prefs[KEY_USER_ID]        ?: return@map null
        // Verifica se a sessão ativa corresponde ao usuário salvo
        if (activeId != userId) return@map null

        UserAccount(
            id                = userId,
            nomeCompleto      = prefs[KEY_NOME_COMPLETO]     ?: "",
            nomeUsuario       = prefs[KEY_NOME_USUARIO]      ?: "",
            email             = prefs[KEY_EMAIL]             ?: "",
            senhaHash         = prefs[KEY_SENHA_HASH]        ?: "",
            bio               = prefs[KEY_BIO]               ?: "",
            avatarUrl         = prefs[KEY_AVATAR_URL],
            bannerUrl         = prefs[KEY_BANNER_URL],
            joinYear          = prefs[KEY_JOIN_YEAR]         ?: 2026,
            seguidores        = prefs[KEY_SEGUIDORES]        ?: 0,
            generosFavoritos  = prefs[KEY_GENEROS_FAVORITOS] ?: emptySet()
        )
    }

    // ─── Operações de gravação ────────────────────────────────────────────

    /**
     * Persiste todos os dados do usuário no DataStore.
     */
    suspend fun saveUser(user: UserAccount) {
        context.userDataStore.edit { prefs ->
            prefs[KEY_USER_ID]           = user.id
            prefs[KEY_NOME_COMPLETO]     = user.nomeCompleto
            prefs[KEY_NOME_USUARIO]      = user.nomeUsuario
            prefs[KEY_EMAIL]             = user.email
            prefs[KEY_SENHA_HASH]        = user.senhaHash
            prefs[KEY_BIO]               = user.bio
            prefs[KEY_JOIN_YEAR]         = user.joinYear
            prefs[KEY_SEGUIDORES]        = user.seguidores
            prefs[KEY_GENEROS_FAVORITOS] = user.generosFavoritos
            user.avatarUrl?.let { prefs[KEY_AVATAR_URL] = it }
                ?: prefs.remove(KEY_AVATAR_URL)
            user.bannerUrl?.let { prefs[KEY_BANNER_URL] = it }
                ?: prefs.remove(KEY_BANNER_URL)
        }
    }

    /**
     * Define o ID da sessão ativa (usuário logado).
     */
    suspend fun setActiveUserId(userId: String?) {
        context.userDataStore.edit { prefs ->
            if (userId != null) {
                prefs[KEY_ACTIVE_USER_ID] = userId
            } else {
                prefs.remove(KEY_ACTIVE_USER_ID)
            }
        }
    }

    /**
     * Atualiza apenas os campos de perfil editáveis.
     */
    suspend fun updateProfile(
        nomeCompleto: String,
        bio: String,
        avatarUrl: String?,
        bannerUrl: String? = null
    ) {
        context.userDataStore.edit { prefs ->
            prefs[KEY_NOME_COMPLETO] = nomeCompleto.trim()
            prefs[KEY_BIO]           = bio.trim()
            if (avatarUrl != null) prefs[KEY_AVATAR_URL] = avatarUrl.trim()
            else prefs.remove(KEY_AVATAR_URL)
            if (bannerUrl != null) prefs[KEY_BANNER_URL] = bannerUrl.trim()
            else prefs.remove(KEY_BANNER_URL)
        }
    }

    /**
     * Remove a sessão ativa (logout) sem apagar os dados do perfil.
     */
    suspend fun logout() {
        context.userDataStore.edit { prefs ->
            prefs.remove(KEY_ACTIVE_USER_ID)
        }
    }

    /**
     * Lê snapshot síncrono do ID do usuário ativo (para inicialização).
     */
    fun getUserDataStore(): DataStore<Preferences> = context.userDataStore
}
