package com.kash.data.remote.dto

data class ProductDto(
    val id: String,
    val name: String,
    val walletId: String,
    val organizationId: String,
    val categoryId: String?,
    val salePriceCents: Long,
    val costPriceCents: Long,
    val currentStock: Int
)

data class SaleDto(
    val id: String,
    val productId: String,
    val transactionId: String,
    val quantity: Int,
    val salePriceCentsEach: Long,
    val costPriceCentsEach: Long,
    val walletId: String,
    val organizationId: String,
    val createdAt: Long
)

data class LossDto(
    val id: String,
    val productId: String,
    val quantity: Int,
    val reason: String,
    val walletId: String,
    val organizationId: String,
    val createdAt: Long
)
