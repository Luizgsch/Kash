package com.kash.data.remote.api

import com.kash.data.remote.dto.AuthDto.GoogleLoginRequest
import com.kash.data.remote.dto.AuthDto.LoginRequest
import com.kash.data.remote.dto.AuthDto.RegisterRequest
import com.kash.data.remote.dto.AuthDto.LoginResponse
import com.kash.data.remote.dto.LossDto
import com.kash.data.remote.dto.ProductDto
import com.kash.data.remote.dto.SaleDto
import com.kash.data.remote.dto.TransactionDto
import retrofit2.http.Body
import retrofit2.http.POST

interface KashApiService {
    @POST("mobile/auth")           suspend fun login(@Body req: LoginRequest): LoginResponse
    @POST("mobile/auth/register")  suspend fun register(@Body req: RegisterRequest): LoginResponse
    @POST("mobile/auth/google")    suspend fun loginWithGoogle(@Body req: GoogleLoginRequest): LoginResponse
    @POST("mobile/transactions") suspend fun postTransaction(@Body dto: TransactionDto)
    @POST("mobile/products")     suspend fun postProduct(@Body dto: ProductDto)
    @POST("mobile/sales")        suspend fun postSale(@Body dto: SaleDto)
    @POST("mobile/losses")       suspend fun postLoss(@Body dto: LossDto)
}
