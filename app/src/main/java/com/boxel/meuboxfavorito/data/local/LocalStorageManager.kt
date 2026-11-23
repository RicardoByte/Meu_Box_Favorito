package com.boxel.meuboxfavorito.data.local

import android.content.Context
import com.boxel.meuboxfavorito.data.local.datastore.UserDataStore
import com.boxel.meuboxfavorito.data.local.room.AppDatabase
import com.boxel.meuboxfavorito.data.local.room.entities.toDomain
import com.boxel.meuboxfavorito.data.local.room.entities.toEntity
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.UserAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Gerenciador central de persistência local do aplicativo.
 *
 * ─── Estratégia de armazenamento ───────────────────────────────────────────
 *
 * [Room] → Cache de Boxes (tabela `boxes` via [AppDatabase])
 *   • Usuários sem conta (guest): ownerUserId = null
 *   • Usuários cadastrados: ownerUserId = userId (boxes vinculadas à conta)
 *
 * [DataStore Preferences] → Perfil do usuário cadastrado ([UserDataStore])
 *   • Armazena campos de perfil: nome, bio, avatar, banner, gêneros favoritos
 *   • Gerencia sessão ativa (ID do usuário logado)
 *
 * [SharedPreferences] → Lista de usuários cadastrados (registro/login local)
 *   • Mantida para suportar múltiplos usuários locais no dispositivo
 *
 * Mantém os mesmos StateFlows públicos para zero regressão nas telas Compose.
 */
object LocalStorageManager {

    // ─── JSON ─────────────────────────────────────────────────────────────
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    // ─── Escopo de coroutines ─────────────────────────────────────────────
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ─── Dependências ─────────────────────────────────────────────────────
    private lateinit var appDatabase: AppDatabase
    private lateinit var userDataStore: UserDataStore
    private lateinit var appContext: Context

    // ─── SharedPreferences (lista de usuários cadastrados) ────────────────
    private const val PREFS_NAME = "meu_box_storage_prefs"
    private const val KEY_USERS  = "key_registered_users"

    // ─── StateFlows públicos (contrato inalterado) ────────────────────────

    private val _usersFlow = MutableStateFlow<List<UserAccount>>(emptyList())
    val usersFlow: StateFlow<List<UserAccount>> = _usersFlow.asStateFlow()

    private val _currentUserFlow = MutableStateFlow<UserAccount?>(null)
    val currentUserFlow: StateFlow<UserAccount?> = _currentUserFlow.asStateFlow()

    private val _customBoxesFlow = MutableStateFlow<List<BoxDetailData>>(emptyList())
    val customBoxesFlow: StateFlow<List<BoxDetailData>> = _customBoxesFlow.asStateFlow()

    // ─── Init ─────────────────────────────────────────────────────────────

    /**
     * Inicializa o gerenciador. Chamado em [MeuBoxApplication.onCreate].
     */
    fun init(context: Context) {
        appContext     = context.applicationContext
        appDatabase    = AppDatabase.getInstance(appContext)
        userDataStore  = UserDataStore(appContext)

        loadRegisteredUsers()
        observeCurrentUser()
        observeBoxes(userId = _currentUserFlow.value?.id)
    }

    // ─── Observadores reativos ────────────────────────────────────────────

    private fun observeCurrentUser() {
        scope.launch {
            userDataStore.currentUserFlow.collectLatest { dsUser ->
                if (dsUser != null && _currentUserFlow.value?.id != dsUser.id) {
                    _currentUserFlow.value = dsUser
                    // Sincroniza na lista de usuários locais
                    mergeUserIntoList(dsUser)
                }
            }
        }
    }

    private fun observeBoxes(userId: String?) {
        scope.launch {
            if (userId != null) {
                appDatabase.boxDao().getBoxesByUserFlow(userId).collectLatest { entities ->
                    _customBoxesFlow.value = entities.map { it.toDomain() }
                }
            } else {
                appDatabase.boxDao().getGuestBoxesFlow().collectLatest { entities ->
                    _customBoxesFlow.value = entities.map { it.toDomain() }
                }
            }
        }
    }

    // ─── Usuários (SharedPreferences) ────────────────────────────────────

    private fun loadRegisteredUsers() {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersJson = prefs.getString(KEY_USERS, null)
        val loadedUsers: List<UserAccount> = if (!usersJson.isNullOrBlank()) {
            try { json.decodeFromString(usersJson) } catch (_: Exception) { emptyList() }
        } else {
            listOf(
                UserAccount(
                    id                = "default_user_1",
                    nomeCompleto      = "User teste",
                    nomeUsuario       = "testeuser",
                    email             = "userteste@boxel.com",
                    senhaHash         = "12345678A",
                    generosFavoritos  = setOf("Ficção Científica", "Suspense", "Neo-Noir"),
                    bio               = "Curando as melhores experiências sci-fi, suspense psicológico e neo-noir.",
                    avatarUrl         = null,
                    joinYear          = 2024,
                    seguidores        = 342
                )
            )
        }
        _usersFlow.value = loadedUsers
        _currentUserFlow.value = loadedUsers.firstOrNull()
    }

    private fun persistUsersList(users: List<UserAccount>) {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USERS, json.encodeToString(users)).apply()
    }

    private fun mergeUserIntoList(user: UserAccount) {
        val list = _usersFlow.value.toMutableList()
        val idx = list.indexOfFirst { it.id == user.id }
        if (idx >= 0) list[idx] = user else list.add(user)
        _usersFlow.value = list
        scope.launch(Dispatchers.IO) { persistUsersList(list) }
    }

    // ─── API pública — Usuários ───────────────────────────────────────────

    /**
     * Persiste um novo usuário cadastrado em SharedPreferences.
     * Se for o usuário ativo, também salva no DataStore.
     */
    fun saveUser(user: UserAccount) {
        mergeUserIntoList(user)
        if (_currentUserFlow.value?.id == user.id) {
            scope.launch { userDataStore.saveUser(user) }
        }
    }

    /**
     * Define o usuário atualmente logado.
     * → DataStore: salva perfil + ativa sessão.
     * → Room: muda observador de boxes para o userId.
     */
    fun setActiveUser(user: UserAccount?) {
        _currentUserFlow.value = user
        scope.launch {
            if (user != null) {
                userDataStore.saveUser(user)
                userDataStore.setActiveUserId(user.id)
            } else {
                userDataStore.logout()
            }
        }
        // Reinicia observador de boxes para o novo contexto
        observeBoxes(userId = user?.id)
    }

    /**
     * Atualiza campos editáveis de perfil do usuário ativo.
     */
    fun updateProfile(
        nomeCompleto: String,
        bio: String,
        avatarUrl: String?,
        bannerUrl: String? = null
    ) {
        val current = _currentUserFlow.value ?: return
        val updated = current.copy(
            nomeCompleto = nomeCompleto.trim(),
            bio          = bio.trim(),
            avatarUrl    = avatarUrl?.trim(),
            bannerUrl    = bannerUrl?.trim()
        )
        _currentUserFlow.value = updated
        scope.launch { userDataStore.updateProfile(nomeCompleto, bio, avatarUrl, bannerUrl) }
        mergeUserIntoList(updated)
    }

    fun getAllUsers(): List<UserAccount> = _usersFlow.value

    fun getActiveUser(): UserAccount? = _currentUserFlow.value

    // ─── API pública — Boxes ──────────────────────────────────────────────

    /**
     * Persiste uma Box no Room.
     * → Guest: ownerUserId = null.
     * → Usuário autenticado: ownerUserId = userId.
     */
    fun saveCustomBox(box: BoxDetailData) {
        val ownerUserId = _currentUserFlow.value?.id
        scope.launch { appDatabase.boxDao().upsertBox(box.toEntity(ownerUserId)) }
        // Atualiza StateFlow otimisticamente para reatividade imediata na UI
        val list = _customBoxesFlow.value.toMutableList()
        val idx = list.indexOfFirst { it.id == box.id }
        if (idx >= 0) list[idx] = box else list.add(0, box)
        _customBoxesFlow.value = list
    }

    fun getCustomBoxes(): List<BoxDetailData> = _customBoxesFlow.value

    /** Alias para saveCustomBox (atualiza se já existir). */
    fun updateCustomBox(box: BoxDetailData) = saveCustomBox(box)

    /**
     * Remove uma Box do Room pelo id e do StateFlow.
     */
    fun deleteCustomBox(boxId: Int) {
        scope.launch { appDatabase.boxDao().deleteBox(boxId) }
        _customBoxesFlow.value = _customBoxesFlow.value.filter { it.id != boxId }
    }

    /**
     * Desloga o usuário ativo mantendo dados persistidos.
     */
    fun logout() {
        _currentUserFlow.value = null
        scope.launch { userDataStore.logout() }
        observeBoxes(userId = null)
    }
}
