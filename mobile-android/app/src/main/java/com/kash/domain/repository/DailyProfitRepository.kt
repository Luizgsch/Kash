package com.kash.domain.repository

import com.kash.data.local.dao.DailyProfitResult
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface DailyProfitRepository {
    fun watchDailyProfit(walletId: String, startMs: Long, endMs: Long): Flow<DailyProfitResult>
    fun watchRecent(walletId: String, limit: Int = 15): Flow<List<ProductTransactionEntity>>
    fun watchByPeriodAndType(walletId: String, startMs: Long, endMs: Long, typeFilter: String?): Flow<List<ProductTransactionEntity>>
    fun watchLosses(walletId: String, limit: Int = 50): Flow<List<ProductTransactionEntity>>
    suspend fun registerSale(product: Product, quantity: Int)
    suspend fun registerLoss(product: Product, quantity: Int, reason: String)
    suspend fun getUnsynced(): List<ProductTransactionEntity>
    suspend fun markSynced(id: String)
}
