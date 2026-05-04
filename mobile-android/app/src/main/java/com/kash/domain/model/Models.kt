package com.kash.domain.model

data class Wallet(
    val id: String,
    val name: String,
    val organizationId: String
)

data class Category(
    val id: String,
    val name: String,
    val walletId: String
)

data class Transaction(
    val id: String,
    val amountCents: Long,
    val type: String,
    val description: String,
    val categoryId: String?,
    val walletId: String,
    val organizationId: String,
    val createdAt: Long
)

data class Product(
    val id: String,
    val name: String,
    val walletId: String,
    val organizationId: String,
    val categoryId: String?,
    val salePriceCents: Long,
    val costPriceCents: Long,
    val currentStock: Int
)

data class Sale(
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

data class Loss(
    val id: String,
    val productId: String,
    val quantity: Int,
    val reason: String,
    val walletId: String,
    val organizationId: String,
    val createdAt: Long
)
