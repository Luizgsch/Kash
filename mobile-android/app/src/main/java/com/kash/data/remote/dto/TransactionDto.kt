package com.kash.data.remote.dto

data class TransactionDto(
    val id: String,
    val amountCents: Long,
    val type: String,
    val description: String,
    val categoryId: String?,
    val walletId: String,
    val organizationId: String,
    val userId: String,
    val createdAt: Long
)
