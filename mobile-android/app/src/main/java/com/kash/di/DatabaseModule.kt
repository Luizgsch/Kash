package com.kash.di

import android.content.Context
import androidx.room.Room
import com.kash.data.local.KashDatabase
import com.kash.data.local.dao.*
import com.kash.data.local.dao.ProductTransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): KashDatabase =
        Room.databaseBuilder(ctx, KashDatabase::class.java, "kash.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideWalletDao(db: KashDatabase): WalletDao         = db.walletDao()
    @Provides fun provideCategoryDao(db: KashDatabase): CategoryDao     = db.categoryDao()
    @Provides fun provideTransactionDao(db: KashDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideProductDao(db: KashDatabase): ProductDao       = db.productDao()
    @Provides fun provideSaleDao(db: KashDatabase): SaleDao             = db.saleDao()
    @Provides fun provideLossDao(db: KashDatabase): LossDao             = db.lossDao()
    @Provides fun provideInsightsDao(db: KashDatabase): InsightsDao              = db.insightsDao()
    @Provides fun provideProductTransactionDao(db: KashDatabase): ProductTransactionDao = db.productTransactionDao()
}
