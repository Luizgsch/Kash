package com.kash.data.local.dao

import androidx.room.*
import com.kash.data.local.entity.ProductEntity
import com.kash.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(p: ProductEntity)
    @Update                                          suspend fun update(p: ProductEntity)
    @Delete                                          suspend fun delete(p: ProductEntity)

    @Query("SELECT * FROM products WHERE walletId = :walletId ORDER BY name ASC")
    fun watchByWallet(walletId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun findById(id: String): ProductEntity?

    @Query("UPDATE products SET currentStock = currentStock - :qty WHERE id = :id")
    suspend fun decrementStock(id: String, qty: Int)

    @Query("UPDATE products SET currentStock = currentStock - :qty WHERE id = :id")
    suspend fun incrementLoss(id: String, qty: Int)

    @Query("SELECT * FROM products WHERE syncStatus = 'PENDING'")
    suspend fun getPending(): List<ProductEntity>

    @Query("UPDATE products SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
