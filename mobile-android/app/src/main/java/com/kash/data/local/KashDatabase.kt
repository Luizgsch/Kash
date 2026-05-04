package com.kash.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kash.data.local.converter.Converters
import com.kash.data.local.dao.*
import com.kash.data.local.entity.*

@Database(
    entities = [
        WalletEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        ProductEntity::class,
        SaleEntity::class,
        LossEntity::class,
        ProductTransactionEntity::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KashDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun lossDao(): LossDao
    abstract fun insightsDao(): InsightsDao
    abstract fun productTransactionDao(): ProductTransactionDao
}
