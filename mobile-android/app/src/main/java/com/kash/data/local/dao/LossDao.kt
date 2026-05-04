package com.kash.data.local.dao

import androidx.room.*
import com.kash.data.local.entity.LossEntity
import com.kash.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LossDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(l: LossEntity)

    @Query("SELECT * FROM losses WHERE walletId = :walletId ORDER BY createdAt DESC")
    fun watchByWallet(walletId: String): Flow<List<LossEntity>>

    @Query("SELECT * FROM losses WHERE syncStatus = 'PENDING'")
    suspend fun getPending(): List<LossEntity>

    @Query("UPDATE losses SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
