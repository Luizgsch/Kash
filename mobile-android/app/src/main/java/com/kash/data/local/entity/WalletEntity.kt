package com.kash.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "wallets",
    indices = [Index("organizationId")]
)
data class WalletEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val organizationId: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val updatedAt: Long = System.currentTimeMillis()
)
