package com.kash.data.repository

import com.kash.data.local.dao.LossDao
import com.kash.data.local.dao.ProductDao
import com.kash.data.local.dao.SaleDao
import com.kash.data.local.entity.LossEntity
import com.kash.data.local.entity.ProductEntity
import com.kash.data.local.entity.SaleEntity
import com.kash.data.local.entity.SyncStatus
import com.kash.domain.model.Product
import com.kash.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val saleDao: SaleDao,
    private val lossDao: LossDao
) : ProductRepository {

    override suspend fun insert(product: Product) = productDao.insert(product.toEntity())
    override suspend fun update(product: Product) = productDao.update(product.toEntity())
    override suspend fun delete(productId: String) {
        val entity = productDao.findById(productId) ?: return
        productDao.delete(entity)
    }
    override suspend fun insertSale(sale: SaleEntity)  = saleDao.insert(sale)
    override suspend fun insertLoss(loss: LossEntity)  = lossDao.insert(loss)
    override suspend fun decrementStock(productId: String, qty: Int) =
        productDao.decrementStock(productId, qty)

    override fun watchByWallet(walletId: String): Flow<List<Product>> =
        productDao.watchByWallet(walletId).map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: String): Product? =
        productDao.findById(id)?.toDomain()

    private fun Product.toEntity() = ProductEntity(
        id             = id,
        name           = name,
        walletId       = walletId,
        organizationId = organizationId,
        categoryId     = categoryId,
        salePriceCents = salePriceCents,
        costPriceCents = costPriceCents,
        currentStock   = currentStock,
        syncStatus     = SyncStatus.PENDING
    )

    private fun ProductEntity.toDomain() = Product(
        id             = id,
        name           = name,
        walletId       = walletId,
        organizationId = organizationId,
        categoryId     = categoryId,
        salePriceCents = salePriceCents,
        costPriceCents = costPriceCents,
        currentStock   = currentStock
    )
}
