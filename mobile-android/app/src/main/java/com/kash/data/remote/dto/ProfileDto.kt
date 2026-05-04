package com.kash.data.remote.dto

data class ProfileDto(
    val id: String,
    val name: String?,
    val email: String?,
    val image: String?,
    val role: String
)

data class UpdateNameRequest(val name: String)
