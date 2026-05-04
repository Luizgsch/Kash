package com.kash.data.local.converter

import androidx.room.TypeConverter
import com.kash.data.local.entity.ProductTransactionType
import com.kash.data.local.entity.SyncStatus
import com.kash.data.local.entity.TransactionType

class Converters {
    @TypeConverter fun fromSyncStatus(v: SyncStatus): String                = v.name
    @TypeConverter fun toSyncStatus(v: String): SyncStatus                  = SyncStatus.valueOf(v)
    @TypeConverter fun fromTxType(v: TransactionType): String               = v.name
    @TypeConverter fun toTxType(v: String): TransactionType                 = TransactionType.valueOf(v)
    @TypeConverter fun fromProductTxType(v: ProductTransactionType): String = v.name
    @TypeConverter fun toProductTxType(v: String): ProductTransactionType   = ProductTransactionType.valueOf(v)
}
