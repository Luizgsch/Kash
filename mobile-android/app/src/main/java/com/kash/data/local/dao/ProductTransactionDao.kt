package com.kash.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kash.data.local.entity.ProductTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Lucro Líquido = Receita de Vendas - CMV de Vendas - Custo de Perdas
 *
 * Receita de Vendas:  SUM(quantity * unitPriceCents) WHERE type = SALE
 * CMV de Vendas:      SUM(quantity * unitCostCents)  WHERE type = SALE
 * Custo de Perdas:    SUM(quantity * unitCostCents)  WHERE type = LOSS
 *
 * Perdas entram apenas pelo custo (mercadoria que foi comprada mas não vendida).
 */
data class DailyProfitResult(
    val totalRevenueCents: Long,
    val totalCOGSCents: Long,
    val totalLossCents: Long
) {
    val netProfitCents: Long get() = totalRevenueCents - totalCOGSCents - totalLossCents
    val grossProfitCents: Long get() = totalRevenueCents - totalCOGSCents
}

@Dao
interface ProductTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: ProductTransactionEntity)

    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN type = 'SALE' THEN quantity * unitPriceCents ELSE 0 END), 0) AS totalRevenueCents,
            COALESCE(SUM(CASE WHEN type = 'SALE' THEN quantity * unitCostCents  ELSE 0 END), 0) AS totalCOGSCents,
            COALESCE(SUM(CASE WHEN type = 'LOSS' THEN quantity * unitCostCents  ELSE 0 END), 0) AS totalLossCents
        FROM product_transactions
        WHERE walletId  = :walletId
          AND createdAt >= :startMs
          AND createdAt <  :endMs
    """)
    fun watchDailyProfit(walletId: String, startMs: Long, endMs: Long): Flow<DailyProfitResult>

    @Query("""
        SELECT * FROM product_transactions
        WHERE walletId = :walletId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    fun watchRecent(walletId: String, limit: Int = 15): Flow<List<ProductTransactionEntity>>

    @Query("""
        SELECT * FROM product_transactions
        WHERE walletId = :walletId
          AND createdAt >= :startMs
          AND createdAt <  :endMs
          AND (:typeFilter IS NULL OR type = :typeFilter)
        ORDER BY createdAt DESC
    """)
    fun watchByPeriodAndType(walletId: String, startMs: Long, endMs: Long, typeFilter: String?): Flow<List<ProductTransactionEntity>>

    @Query("""
        SELECT * FROM product_transactions
        WHERE walletId = :walletId AND type = 'LOSS'
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    fun watchLosses(walletId: String, limit: Int = 50): Flow<List<ProductTransactionEntity>>

    @Query("SELECT * FROM product_transactions WHERE synced = 0")
    suspend fun getUnsynced(): List<ProductTransactionEntity>

    @Query("UPDATE product_transactions SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
