package com.boxel.meuboxfavorito

import android.app.Application
import com.boxel.meuboxfavorito.data.local.LocalStorageManager
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository

/**
 * Classe Application do Meu Box Favorito.
 *
 * Responsável por inicializar os componentes globais de persistência:
 * - [LocalStorageManager]: Room (cache de Boxes) + DataStore (perfil de usuário)
 * - [BoxDetailRepository]: carrega boxes customizadas do storage ao iniciar
 */
class MeuBoxApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Inicializa Room + DataStore + SharedPreferences
        LocalStorageManager.init(this)

        // 2. Carrega boxes customizadas do Room para o repositório in-memory
        BoxDetailRepository.loadCustomBoxesFromStorage()
    }
}
