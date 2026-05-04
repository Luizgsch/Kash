package com.kash.data.remote.dto

data class TransactionResponseDto(
    val id: String,
    val amountCents: Long,
    val type: String,
    val description: String,
    val categoryId: String?,
    val categoryName: String,
    val walletId: String,
    val createdAt: Long
)
