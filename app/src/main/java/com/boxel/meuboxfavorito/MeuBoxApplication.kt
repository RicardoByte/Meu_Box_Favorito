package com.boxel.meuboxfavorito

import android.app.Application
import com.boxel.meuboxfavorito.data.local.LocalStorageManager
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository

class MeuBoxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocalStorageManager.init(this)
        BoxDetailRepository.loadCustomBoxesFromStorage()
    }
}
