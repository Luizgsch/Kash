package com.kash.data.local.dao

import androidx.room.*
import com.kash.data.local.entity.SyncStatus
import com.kash.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(w: WalletEntity)
    @Update                                          suspend fun update(w: WalletEntity)
    @Delete                                          suspend fun delete(w: WalletEntity)

    @Query("SELECT * FROM wallets WHERE organizationId = :orgId ORDER BY name ASC")
    fun watchByOrg(orgId: String): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets WHERE syncStatus = 'PENDING'")
    suspend fun getPending(): List<WalletEntity>

    @Query("UPDATE wallets SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
