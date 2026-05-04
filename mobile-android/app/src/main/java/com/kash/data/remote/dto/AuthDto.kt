package com.kash.data.remote.dto

object AuthDto {
    data class LoginRequest(val email: String, val password: String)
    data class GoogleLoginRequest(val idToken: String)
    data class RegisterRequest(val email: String, val password: String, val name: String)

    data class LoginResponse(
        val token: String,
        val userId: String,
        val organizationId: String,
        val defaultWalletId: String,
        val name: String,
        val email: String
    )
}
