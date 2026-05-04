package com.kash.data.local.dao

import androidx.room.*
import com.kash.data.local.entity.SaleEntity
import com.kash.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(s: SaleEntity)

    @Query("SELECT * FROM sales WHERE walletId = :walletId ORDER BY createdAt DESC")
    fun watchByWallet(walletId: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE syncStatus = 'PENDING'")
    suspend fun getPending(): List<SaleEntity>

    @Query("UPDATE sales SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
