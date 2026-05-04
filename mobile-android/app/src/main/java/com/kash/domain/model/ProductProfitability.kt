package com.kash.domain.model

import com.kash.data.local.dao.ProductProfitabilityResult

data class ProductProfitability(
    val productId: String,
    val productName: String,
    val totalQuantitySold: Int,
    val netProfitCents: Long,
    val totalRevenueCents: Long,
    val totalLossCents: Long,
    val marginPercent: Float
) {
    companion object {
        fun from(r: ProductProfitabilityResult) = ProductProfitability(
            productId         = r.productId,
            productName       = r.productName,
            totalQuantitySold = r.totalQuantitySold,
            netProfitCents    = r.netProfitCents,
            totalRevenueCents = r.totalRevenueCents,
            totalLossCents    = r.totalLossCents,
            marginPercent     = if (r.totalRevenueCents > 0)
                (r.netProfitCents.toFloat() / r.totalRevenueCents) * 100f
            else 0f
        )
    }
}

data class ProfitabilityInsights(
    val starProducts: List<ProductProfitability>,
    val volumeProducts: List<ProductProfitability>,
    val totalNetProfitCents: Long,
    val totalLossCents: Long,
    val periodStartMs: Long,
    val periodEndMs: Long
)
