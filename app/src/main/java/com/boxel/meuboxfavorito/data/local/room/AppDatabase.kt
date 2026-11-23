package com.boxel.meuboxfavorito.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.boxel.meuboxfavorito.data.local.room.converters.Converters
import com.boxel.meuboxfavorito.data.local.room.dao.BoxDao
import com.boxel.meuboxfavorito.data.local.room.entities.BoxEntity

/**
 * Banco de dados Room principal do aplicativo.
 *
 * Responsabilidade: cache local de Boxes.
 * - Usuários sem conta (guest): boxes são armazenados com ownerUserId = null.
 * - Usuários cadastrados: boxes associados com ownerUserId = userId.
 *
 * Para dados de perfil de usuários cadastrados, use [UserDataStore].
 */
@Database(
    entities = [BoxEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun boxDao(): BoxDao

    companion object {
        private const val DATABASE_NAME = "meu_box_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna a instância singleton do banco de dados.
         * Thread-safe via double-checked locking.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
