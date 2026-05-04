package com.kash.domain.usecase

import com.kash.domain.model.UserSession
import com.kash.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> =
        repo.login(email, password)

    suspend fun withGoogle(idToken: String): Result<UserSession> =
        repo.loginWithGoogle(idToken)

    suspend fun register(email: String, password: String, name: String): Result<UserSession> =
        repo.register(email, password, name)
}
