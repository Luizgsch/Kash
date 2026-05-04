package com.kash.domain.usecase

import com.kash.data.local.entity.LossEntity
import com.kash.domain.model.Product
import com.kash.domain.repository.ProductRepository
import javax.inject.Inject

class RegisterLossUseCase @Inject constructor(
    private val productRepo: ProductRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int, reason: String) {
        productRepo.insertLoss(
            LossEntity(
                productId      = product.id,
                quantity       = quantity,
                reason         = reason,
                walletId       = product.walletId,
                organizationId = product.organizationId
            )
        )
        productRepo.decrementStock(product.id, quantity)
    }
}
