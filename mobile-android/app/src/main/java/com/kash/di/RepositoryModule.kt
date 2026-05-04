package com.kash.di

import com.kash.data.repository.AuthRepositoryImpl
import com.kash.data.repository.DailyProfitRepositoryImpl
import com.kash.data.repository.InsightsRepositoryImpl
import com.kash.data.repository.ProductRepositoryImpl
import com.kash.data.repository.TransactionRepositoryImpl
import com.kash.domain.repository.AuthRepository
import com.kash.domain.repository.DailyProfitRepository
import com.kash.domain.repository.InsightsRepository
import com.kash.domain.repository.ProductRepository
import com.kash.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindAuthRepo(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindTransactionRepo(impl: TransactionRepositoryImpl): TransactionRepository
    @Binds @Singleton abstract fun bindProductRepo(impl: ProductRepositoryImpl): ProductRepository
    @Binds @Singleton abstract fun bindInsightsRepo(impl: InsightsRepositoryImpl): InsightsRepository
    @Binds @Singleton abstract fun bindDailyProfitRepo(impl: DailyProfitRepositoryImpl): DailyProfitRepository
}
