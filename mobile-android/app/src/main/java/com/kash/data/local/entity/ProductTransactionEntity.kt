package com.kash.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class ProductTransactionType { SALE, LOSS }

/**
 * Registro unificado de vendas e perdas de produto.
 * Campos de preço são snapshots no momento do registro (preço pode mudar depois).
 * synced=false indica pendência de envio ao backend.
 */
@Entity(
    tableName = "product_transactions",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("productId"),
        Index("createdAt"),
        Index("walletId"),
        Index("synced")
    ]
)
data class ProductTransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val productName: String,        // denormalizado para exibição sem JOIN
    val type: ProductTransactionType,
    val quantity: Int,
    val unitPriceCents: Long,       // preço de venda no momento do registro
    val unitCostCents: Long,        // preço de custo no momento do registro
    val reason: String = "",        // motivo da perda (vazio para vendas)
    val walletId: String,
    val organizationId: String,
    val synced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
