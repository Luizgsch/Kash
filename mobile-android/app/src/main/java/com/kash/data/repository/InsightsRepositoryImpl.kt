package com.kash.data.repository

import com.kash.data.local.dao.InsightsDao
import com.kash.data.local.dao.ProductProfitabilityResult
import com.kash.domain.repository.InsightsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsightsRepositoryImpl @Inject constructor(
    private val dao: InsightsDao
) : InsightsRepository {
    override fun watchProfitability(
        walletId: String,
        orgId: String,
        startMs: Long,
        endMs: Long
    ): Flow<List<ProductProfitabilityResult>> =
        dao.watchProfitability(walletId, orgId, startMs, endMs)
}
