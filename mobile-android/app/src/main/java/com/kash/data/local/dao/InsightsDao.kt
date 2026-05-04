package com.kash.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class ProductProfitabilityResult(
    val productId: String,
    val productName: String,
    val totalQuantitySold: Int,
    val totalRevenueCents: Long,
    val totalCostCents: Long,
    val totalLossCents: Long,
    val netProfitCents: Long
)

@Dao
interface InsightsDao {

    @Query("""
        SELECT
            p.id                                                                    AS productId,
            p.name                                                                  AS productName,
            COALESCE(SUM(s.quantity), 0)                                            AS totalQuantitySold,
            COALESCE(SUM(s.salePriceCentsEach * s.quantity), 0)                     AS totalRevenueCents,
            COALESCE(SUM(s.costPriceCentsEach * s.quantity), 0)                     AS totalCostCents,
            COALESCE((SELECT SUM(l2.quantity) * p.costPriceCents
                      FROM losses l2
                      WHERE l2.productId = p.id
                        AND l2.walletId  = :walletId
                        AND l2.createdAt BETWEEN :startMs AND :endMs), 0)           AS totalLossCents,
            COALESCE(SUM(s.salePriceCentsEach * s.quantity), 0)
              - COALESCE(SUM(s.costPriceCentsEach * s.quantity), 0)
              - COALESCE((SELECT SUM(l2.quantity) * p.costPriceCents
                          FROM losses l2
                          WHERE l2.productId = p.id
                            AND l2.walletId  = :walletId
                            AND l2.createdAt BETWEEN :startMs AND :endMs), 0)       AS netProfitCents
        FROM products p
        LEFT JOIN sales s ON s.productId = p.id
                          AND s.walletId  = :walletId
                          AND s.createdAt BETWEEN :startMs AND :endMs
        WHERE p.walletId       = :walletId
          AND p.organizationId = :orgId
        GROUP BY p.id
    """)
    fun watchProfitability(
        walletId: String,
        orgId: String,
        startMs: Long,
        endMs: Long
    ): Flow<List<ProductProfitabilityResult>>

    @Query("""
        SELECT COALESCE(SUM(l.quantity * p.costPriceCents), 0)
        FROM losses l
        INNER JOIN products p ON p.id = l.productId
        WHERE l.walletId = :walletId
          AND l.createdAt BETWEEN :startMs AND :endMs
    """)
    fun watchPeriodLossCents(walletId: String, startMs: Long, endMs: Long): Flow<Long>
}
