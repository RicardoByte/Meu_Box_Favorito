package com.boxel.meuboxfavorito.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boxel.meuboxfavorito.data.local.room.entities.BoxEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operações de Boxes no Room Database.
 *
 * Expõe Flow reativo para que qualquer alteração reflita imediatamente nas telas.
 */
@Dao
interface BoxDao {

    /**
     * Insere ou substitui um box no cache.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBox(box: BoxEntity): Long

    /**
     * Insere ou substitui uma lista de boxes.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBoxes(boxes: List<BoxEntity>): List<Long>

    /**
     * Retorna todos os boxes como Flow reativo.
     * Emite nova lista sempre que qualquer box for inserido, atualizado ou removido.
     */
    @Query("SELECT * FROM boxes ORDER BY id DESC")
    fun getAllBoxesFlow(): Flow<List<BoxEntity>>

    /**
     * Retorna todos os boxes de um usuário específico.
     */
    @Query("SELECT * FROM boxes WHERE ownerUserId = :userId ORDER BY id DESC")
    fun getBoxesByUserFlow(userId: String): Flow<List<BoxEntity>>

    /**
     * Retorna boxes de convidados (sem usuário vinculado).
     */
    @Query("SELECT * FROM boxes WHERE ownerUserId IS NULL ORDER BY id DESC")
    fun getGuestBoxesFlow(): Flow<List<BoxEntity>>

    /**
     * Busca um box pelo id.
     */
    @Query("SELECT * FROM boxes WHERE id = :boxId LIMIT 1")
    suspend fun getBoxById(boxId: Int): BoxEntity?

    /**
     * Remove um box pelo id.
     */
    @Query("DELETE FROM boxes WHERE id = :boxId")
    suspend fun deleteBox(boxId: Int): Int

    /**
     * Remove todos os boxes de um usuário específico.
     */
    @Query("DELETE FROM boxes WHERE ownerUserId = :userId")
    suspend fun deleteBoxesByUser(userId: String): Int

    /**
     * Remove todos os boxes de convidados.
     */
    @Query("DELETE FROM boxes WHERE ownerUserId IS NULL")
    suspend fun deleteGuestBoxes(): Int
}
