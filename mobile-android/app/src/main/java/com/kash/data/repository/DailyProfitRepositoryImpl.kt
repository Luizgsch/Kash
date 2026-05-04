package com.kash.data.repository

import com.kash.data.local.dao.DailyProfitResult
import com.kash.data.local.dao.ProductTransactionDao
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.data.local.entity.ProductTransactionType
import com.kash.domain.model.Product
import com.kash.domain.repository.DailyProfitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyProfitRepositoryImpl @Inject constructor(
    private val dao: ProductTransactionDao
) : DailyProfitRepository {

    override fun watchDailyProfit(walletId: String, startMs: Long, endMs: Long): Flow<DailyProfitResult> =
        dao.watchDailyProfit(walletId, startMs, endMs)

    override fun watchRecent(walletId: String, limit: Int): Flow<List<ProductTransactionEntity>> =
        dao.watchRecent(walletId, limit)

    override suspend fun registerSale(product: Product, quantity: Int) {
        dao.insert(
            ProductTransactionEntity(
                productId      = product.id,
                productName    = product.name,
                type           = ProductTransactionType.SALE,
                quantity       = quantity,
                unitPriceCents = product.salePriceCents,
                unitCostCents  = product.costPriceCents,
                walletId       = product.walletId,
                organizationId = product.organizationId
            )
        )
    }

    override suspend fun registerLoss(product: Product, quantity: Int, reason: String) {
        dao.insert(
            ProductTransactionEntity(
                productId      = product.id,
                productName    = product.name,
                type           = ProductTransactionType.LOSS,
                quantity       = quantity,
                unitPriceCents = product.salePriceCents,
                unitCostCents  = product.costPriceCents,
                reason         = reason,
                walletId       = product.walletId,
                organizationId = product.organizationId
            )
        )
    }

    override fun watchByPeriodAndType(walletId: String, startMs: Long, endMs: Long, typeFilter: String?): Flow<List<ProductTransactionEntity>> =
        dao.watchByPeriodAndType(walletId, startMs, endMs, typeFilter)

    override fun watchLosses(walletId: String, limit: Int): Flow<List<ProductTransactionEntity>> =
        dao.watchLosses(walletId, limit)

    override suspend fun getUnsynced(): List<ProductTransactionEntity> = dao.getUnsynced()
    override suspend fun markSynced(id: String) = dao.markSynced(id)
}
