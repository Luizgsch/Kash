package com.kash.domain.repository

import com.kash.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserSession>
    suspend fun loginWithGoogle(idToken: String): Result<UserSession>
    suspend fun register(email: String, password: String, name: String): Result<UserSession>
    suspend fun logout()
    fun watchSession(): Flow<UserSession?>
}
