package com.kash.domain.usecase

import com.kash.data.local.entity.SaleEntity
import com.kash.data.local.entity.TransactionEntity
import com.kash.data.local.entity.TransactionType
import com.kash.domain.model.Product
import com.kash.domain.repository.ProductRepository
import com.kash.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

class AddSaleUseCase @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val productRepo: ProductRepository
) {
    suspend operator fun invoke(
        product: Product,
        quantity: Int,
        userId: String
    ) {
        val txId       = UUID.randomUUID().toString()
        val totalCents = product.salePriceCents * quantity

        transactionRepo.insert(
            TransactionEntity(
                id             = txId,
                amountCents    = totalCents,
                type           = TransactionType.INFLOW,
                description    = "${product.name} x$quantity",
                walletId       = product.walletId,
                organizationId = product.organizationId,
                userId         = userId
            )
        )

        productRepo.insertSale(
            SaleEntity(
                productId          = product.id,
                transactionId      = txId,
                quantity           = quantity,
                salePriceCentsEach = product.salePriceCents,
                costPriceCentsEach = product.costPriceCents,
                walletId           = product.walletId,
                organizationId     = product.organizationId
            )
        )

        productRepo.decrementStock(product.id, quantity)
    }
}
