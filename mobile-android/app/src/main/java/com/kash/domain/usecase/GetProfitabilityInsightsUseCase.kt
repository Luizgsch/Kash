package com.kash.domain.usecase

import com.kash.domain.model.ProductProfitability
import com.kash.domain.model.ProfitabilityInsights
import com.kash.domain.repository.InsightsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProfitabilityInsightsUseCase @Inject constructor(
    private val repo: InsightsRepository
) {
    operator fun invoke(
        walletId: String,
        orgId: String,
        startMs: Long,
        endMs: Long
    ): Flow<ProfitabilityInsights> =
        repo.watchProfitability(walletId, orgId, startMs, endMs)
            .map { results ->
                val mapped = results.map(ProductProfitability::from)
                ProfitabilityInsights(
                    starProducts        = mapped.sortedByDescending { it.netProfitCents }.take(5),
                    volumeProducts      = mapped.sortedByDescending { it.totalQuantitySold }.take(5),
                    totalNetProfitCents = mapped.sumOf { it.netProfitCents },
                    totalLossCents      = mapped.sumOf { it.totalLossCents },
                    periodStartMs       = startMs,
                    periodEndMs         = endMs
                )
            }
}
