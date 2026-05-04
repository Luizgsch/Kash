package com.kash.data.remote.api

import com.kash.data.remote.dto.AuthDto.GoogleLoginRequest
import com.kash.data.remote.dto.AuthDto.LoginRequest
import com.kash.data.remote.dto.AuthDto.RegisterRequest
import com.kash.data.remote.dto.AuthDto.LoginResponse
import com.kash.data.remote.dto.CategoryManageDto
import com.kash.data.remote.dto.CreateCategoryRequest
import com.kash.data.remote.dto.CreateWalletRequest
import com.kash.data.remote.dto.ProfileDto
import com.kash.data.remote.dto.RenameWalletRequest
import com.kash.data.remote.dto.TransactionDto
import com.kash.data.remote.dto.TransactionResponseDto
import com.kash.data.remote.dto.UpdateNameRequest
import com.kash.data.remote.dto.WalletDto
import retrofit2.http.*

interface KashApiService {
    // Auth
    @POST("mobile/auth")          suspend fun login(@Body req: LoginRequest): LoginResponse
    @POST("mobile/auth/register") suspend fun register(@Body req: RegisterRequest): LoginResponse
    @POST("mobile/auth/google")   suspend fun loginWithGoogle(@Body req: GoogleLoginRequest): LoginResponse

    // Transactions
    @GET("mobile/transactions")
    suspend fun getTransactions(@Query("walletId") walletId: String? = null, @Query("limit") limit: Int = 50): List<TransactionResponseDto>

    @POST("mobile/transactions")
    suspend fun postTransaction(@Body dto: TransactionDto)

    // Wallets / Spaces
    @GET("mobile/wallets")
    suspend fun getWallets(): List<WalletDto>

    @POST("mobile/wallets")
    suspend fun createWallet(@Body req: CreateWalletRequest): WalletDto

    @PATCH("mobile/wallets/{id}")
    suspend fun renameWallet(@Path("id") id: String, @Body req: RenameWalletRequest): WalletDto

    @DELETE("mobile/wallets/{id}")
    suspend fun deleteWallet(@Path("id") id: String)

    // Categories
    @GET("mobile/categories")
    suspend fun getCategories(@Query("walletId") walletId: String): List<CategoryManageDto>

    @POST("mobile/categories")
    suspend fun createCategory(@Body req: CreateCategoryRequest): CategoryManageDto

    @DELETE("mobile/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String)

    // Profile
    @GET("mobile/profile")
    suspend fun getProfile(): ProfileDto

    @PATCH("mobile/profile")
    suspend fun updateProfile(@Body req: UpdateNameRequest): ProfileDto
}
