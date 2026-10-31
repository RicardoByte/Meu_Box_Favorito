package com.boxel.meuboxfavorito.data.local

import android.content.Context
import android.content.SharedPreferences
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Gerenciador central de persistência local do aplicativo.
 *
 * Utiliza [SharedPreferences] integrado com [kotlinx.serialization] para
 * armazenar de forma persistente e síncrona:
 * 1. Lista de usuários cadastrados (inputs do formulário de cadastro)
 * 2. Sessão do usuário autenticado no momento
 * 3. Boxes criadas pelo usuário (inputs da tela de criação de box)
 *
 * Expõe StateFlows reativos para que qualquer alteração nos dados reflita
 * imediatamente em todas as telas conectadas.
 */
object LocalStorageManager {

    private const val PREFS_NAME = "meu_box_storage_prefs"
    private const val KEY_USERS = "key_registered_users"
    private const val KEY_ACTIVE_USER_ID = "key_active_user_id"
    private const val KEY_CUSTOM_BOXES = "key_custom_boxes"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private var preferences: SharedPreferences? = null

    // ─── StateFlows Reativos ──────────────────────────────────────────────
    private val _usersFlow = MutableStateFlow<List<UserAccount>>(emptyList())
    val usersFlow: StateFlow<List<UserAccount>> = _usersFlow.asStateFlow()

    private val _currentUserFlow = MutableStateFlow<UserAccount?>(null)
    val currentUserFlow: StateFlow<UserAccount?> = _currentUserFlow.asStateFlow()

    private val _customBoxesFlow = MutableStateFlow<List<BoxDetailData>>(emptyList())
    val customBoxesFlow: StateFlow<List<BoxDetailData>> = _customBoxesFlow.asStateFlow()

    /**
     * Inicializa o gerenciador com o contexto da aplicação.
     * Deve ser chamado no onCreate da Application ou MainActivity.
     */
    fun init(context: Context) {
        if (preferences == null) {
            preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadAll()
        }
    }

    private fun loadAll() {
        val prefs = preferences ?: return

        // 1. Carregar Usuários
        val usersJson = prefs.getString(KEY_USERS, null)
        val loadedUsers: List<UserAccount> = if (!usersJson.isNullOrBlank()) {
            try {
                json.decodeFromString(usersJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            // Conta padrão inicial (Ricardo Byte) para uso imediato
            listOf(
                UserAccount(
                    id = "default_user_1",
                    nomeCompleto = "Ricardo Byte",
                    nomeUsuario = "ricardobyte",
                    email = "ricardo@boxel.com",
                    senhaHash = "12345678A",
                    generosFavoritos = setOf("Ficção Científica", "Suspense", "Neo-Noir"),
                    bio = "Curando as melhores experiências sci-fi, suspense psicológico e neo-noir.",
                    avatarUrl = null,
                    joinYear = 2024,
                    seguidores = 342
                )
            )
        }
        _usersFlow.value = loadedUsers

        // 2. Carregar Usuário Ativo
        val activeUserId = prefs.getString(KEY_ACTIVE_USER_ID, null)
        val activeUser = loadedUsers.firstOrNull { it.id == activeUserId } ?: loadedUsers.firstOrNull()
        _currentUserFlow.value = activeUser

        // 3. Carregar Boxes Customizadas do Usuário
        val boxesJson = prefs.getString(KEY_CUSTOM_BOXES, null)
        val loadedBoxes: List<BoxDetailData> = if (!boxesJson.isNullOrBlank()) {
            try {
                json.decodeFromString(boxesJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
        _customBoxesFlow.value = loadedBoxes
    }

    // ─── Gerenciamento de Usuários ────────────────────────────────────────

    /**
     * Persiste um novo usuário cadastrado.
     */
    fun saveUser(user: UserAccount) {
        val prefs = preferences ?: return
        val currentList = _usersFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == user.id || it.email.equals(user.email, ignoreCase = true) }
        if (index >= 0) {
            currentList[index] = user
        } else {
            currentList.add(user)
        }
        _usersFlow.value = currentList

        prefs.edit().putString(KEY_USERS, json.encodeToString(currentList)).apply()
    }

    /**
     * Define o usuário atualmente logado no app.
     */
    fun setActiveUser(user: UserAccount?) {
        val prefs = preferences ?: return
        _currentUserFlow.value = user
        prefs.edit().putString(KEY_ACTIVE_USER_ID, user?.id).apply()
    }

    /**
     * Atualiza os dados de perfil do usuário atual e persiste as alterações.
     */
    fun updateProfile(nomeCompleto: String, bio: String, avatarUrl: String?) {
        val current = _currentUserFlow.value ?: return
        val updated = current.copy(
            nomeCompleto = nomeCompleto.trim(),
            bio = bio.trim(),
            avatarUrl = avatarUrl?.trim()
        )
        saveUser(updated)
        setActiveUser(updated)
    }

    /**
     * Retorna a lista de todos os usuários cadastrados.
     */
    fun getAllUsers(): List<UserAccount> = _usersFlow.value

    /**
     * Retorna o usuário ativo ou null se deslogado.
     */
    fun getActiveUser(): UserAccount? = _currentUserFlow.value

    // ─── Gerenciamento de Boxes do Usuário ────────────────────────────────

    /**
     * Persiste uma nova Box criada pelo usuário.
     */
    fun saveCustomBox(box: BoxDetailData) {
        val prefs = preferences ?: return
        val currentBoxes = _customBoxesFlow.value.toMutableList()
        val index = currentBoxes.indexOfFirst { it.id == box.id }
        if (index >= 0) {
            currentBoxes[index] = box
        } else {
            currentBoxes.add(0, box) // Adiciona no início
        }
        _customBoxesFlow.value = currentBoxes

        prefs.edit().putString(KEY_CUSTOM_BOXES, json.encodeToString(currentBoxes)).apply()
    }

    /**
     * Retorna a lista de todas as boxes criadas pelo usuário persistidas.
     */
    fun getCustomBoxes(): List<BoxDetailData> = _customBoxesFlow.value

    /**
     * Atualiza uma box existente (ex: resenhas adicionadas, likes, etc.).
     */
    fun updateCustomBox(box: BoxDetailData) {
        saveCustomBox(box)
    }

    /**
     * Desloga o usuário atual mantendo os dados salvos no dispositivo.
     */
    fun logout() {
        val prefs = preferences ?: return
        _currentUserFlow.value = null
        prefs.edit().remove(KEY_ACTIVE_USER_ID).apply()
    }
}
