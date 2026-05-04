package com.kash.domain.repository

import com.kash.data.local.dao.ProductProfitabilityResult
import com.kash.data.local.entity.LossEntity
import com.kash.data.local.entity.SaleEntity
import com.kash.data.local.entity.TransactionEntity
import com.kash.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insert(transaction: TransactionEntity)
    suspend fun update(transaction: TransactionEntity)
    suspend fun delete(transaction: TransactionEntity)
    fun watchByWallet(walletId: String): Flow<List<TransactionEntity>>
}

interface ProductRepository {
    suspend fun insert(product: Product)
    suspend fun update(product: Product)
    suspend fun delete(productId: String)
    suspend fun insertSale(sale: SaleEntity)
    suspend fun insertLoss(loss: LossEntity)
    suspend fun decrementStock(productId: String, qty: Int)
    fun watchByWallet(walletId: String): Flow<List<Product>>
    suspend fun findById(id: String): Product?
}

interface InsightsRepository {
    fun watchProfitability(
        walletId: String,
        orgId: String,
        startMs: Long,
        endMs: Long
    ): Flow<List<ProductProfitabilityResult>>
}
