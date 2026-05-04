package com.kash.data.local.dao

import androidx.room.*
import com.kash.data.local.entity.CategoryEntity
import com.kash.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(c: CategoryEntity)
    @Update                                          suspend fun update(c: CategoryEntity)
    @Delete                                          suspend fun delete(c: CategoryEntity)

    @Query("SELECT * FROM categories WHERE walletId = :walletId ORDER BY name ASC")
    fun watchByWallet(walletId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE syncStatus = 'PENDING'")
    suspend fun getPending(): List<CategoryEntity>

    @Query("UPDATE categories SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
