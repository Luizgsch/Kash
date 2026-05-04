package com.kash.domain.model

data class UserSession(
    val token: String,
    val userId: String,
    val orgId: String,
    val activeWalletId: String,
    val userName: String,
    val userEmail: String
)

sealed interface AuthState {
    data object Loading         : AuthState
    data object Unauthenticated : AuthState
    data class  Authenticated(val session: UserSession) : AuthState
}
