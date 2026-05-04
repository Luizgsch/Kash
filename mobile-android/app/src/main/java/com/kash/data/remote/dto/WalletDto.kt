package com.kash.data.remote.dto

data class WalletDto(
    val id: String,
    val name: String,
    val transactionCount: Int,
    val categories: List<CategoryDto>
)

data class CategoryDto(
    val id: String,
    val name: String,
    val transactionCount: Int
)

data class CreateWalletRequest(val name: String)
data class RenameWalletRequest(val name: String)
