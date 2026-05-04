package com.kash.data.repository

import com.kash.data.local.UserPreferences
import com.kash.data.remote.api.KashApiService
import com.kash.data.remote.dto.AuthDto
import com.kash.domain.model.UserSession
import com.kash.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: KashApiService,
    private val prefs: UserPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserSession> = try {
        val response = api.login(AuthDto.LoginRequest(email, password))
        val session  = UserSession(
            token          = response.token,
            userId         = response.userId,
            orgId          = response.organizationId,
            activeWalletId = response.defaultWalletId,
            userName       = response.name,
            userEmail      = response.email
        )
        prefs.save(session)
        Result.success(session)
    } catch (e: HttpException) {
        val msg = when (e.code()) {
            401  -> "E-mail ou senha incorretos"
            403  -> "Acesso negado"
            else -> "Erro do servidor (${e.code()})"
        }
        Result.failure(Exception(msg))
    } catch (e: UnknownHostException) {
        Result.failure(Exception("Sem conexão com o servidor"))
    } catch (e: SocketTimeoutException) {
        Result.failure(Exception("Tempo esgotado — tente novamente"))
    } catch (e: Exception) {
        Result.failure(Exception("Erro: ${e.message}"))
    }

    override suspend fun loginWithGoogle(idToken: String): Result<UserSession> = try {
        val response = api.loginWithGoogle(AuthDto.GoogleLoginRequest(idToken))
        val session  = UserSession(
            token          = response.token,
            userId         = response.userId,
            orgId          = response.organizationId,
            activeWalletId = response.defaultWalletId,
            userName       = response.name,
            userEmail      = response.email
        )
        prefs.save(session)
        Result.success(session)
    } catch (e: HttpException) {
        val msg = when (e.code()) {
            401  -> runCatching { e.response()?.errorBody()?.string() }
                        .getOrNull()?.let { extractError(it) } ?: "Conta não encontrada"
            403  -> "Conta sem organização. Complete o onboarding no site."
            else -> "Erro do servidor (${e.code()})"
        }
        Result.failure(Exception(msg))
    } catch (e: UnknownHostException) {
        Result.failure(Exception("Sem conexão com o servidor"))
    } catch (e: SocketTimeoutException) {
        Result.failure(Exception("Tempo esgotado — tente novamente"))
    } catch (e: Exception) {
        Result.failure(Exception("Erro: ${e.message}"))
    }

    override suspend fun register(email: String, password: String, name: String): Result<UserSession> = try {
        val response = api.register(AuthDto.RegisterRequest(email, password, name))
        val session  = UserSession(
            token          = response.token,
            userId         = response.userId,
            orgId          = response.organizationId,
            activeWalletId = response.defaultWalletId,
            userName       = response.name,
            userEmail      = response.email
        )
        prefs.save(session)
        Result.success(session)
    } catch (e: HttpException) {
        val msg = when (e.code()) {
            409  -> "E-mail já cadastrado"
            400  -> "E-mail e senha obrigatórios"
            else -> "Erro do servidor (${e.code()})"
        }
        Result.failure(Exception(msg))
    } catch (e: UnknownHostException) {
        Result.failure(Exception("Sem conexão com o servidor"))
    } catch (e: SocketTimeoutException) {
        Result.failure(Exception("Tempo esgotado — tente novamente"))
    } catch (e: Exception) {
        Result.failure(Exception("Erro: ${e.message}"))
    }

    override suspend fun logout()                      { prefs.clear() }
    override fun watchSession(): Flow<UserSession?>    = prefs.session

    private fun extractError(body: String): String? =
        Regex(""""error"\s*:\s*"([^"]+)"""").find(body)?.groupValues?.get(1)
}
