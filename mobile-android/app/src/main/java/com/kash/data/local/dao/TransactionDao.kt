package com.kash.data.local.dao

import androidx.room.*
import com.kash.data.local.entity.SyncStatus
import com.kash.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(t: TransactionEntity)
    @Update                                          suspend fun update(t: TransactionEntity)
    @Delete                                          suspend fun delete(t: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE walletId = :walletId ORDER BY createdAt DESC")
    fun watchByWallet(walletId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE walletId = :walletId AND createdAt BETWEEN :startMs AND :endMs ORDER BY createdAt DESC")
    fun watchByWalletAndPeriod(walletId: String, startMs: Long, endMs: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE syncStatus = 'PENDING'")
    suspend fun getPending(): List<TransactionEntity>

    @Query("UPDATE transactions SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("SELECT COALESCE(SUM(amountCents),0) FROM transactions WHERE walletId = :walletId AND type = 'INFLOW'  AND createdAt BETWEEN :startMs AND :endMs")
    fun watchTotalInflow(walletId: String, startMs: Long, endMs: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(amountCents),0) FROM transactions WHERE walletId = :walletId AND type = 'OUTFLOW' AND createdAt BETWEEN :startMs AND :endMs")
    fun watchTotalOutflow(walletId: String, startMs: Long, endMs: Long): Flow<Long>
}
