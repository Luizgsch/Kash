package com.kash.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.kash.data.local.dao.LossDao
import com.kash.data.local.dao.ProductDao
import com.kash.data.local.dao.SaleDao
import com.kash.data.local.dao.TransactionDao
import com.kash.data.local.entity.SyncStatus
import com.kash.data.remote.api.KashApiService
import com.kash.data.remote.dto.LossDto
import com.kash.data.remote.dto.ProductDto
import com.kash.data.remote.dto.SaleDto
import com.kash.data.remote.dto.TransactionDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val saleDao: SaleDao,
    private val lossDao: LossDao,
    private val api: KashApiService
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            syncTransactions()
            syncProducts()
            syncSales()
            syncLosses()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun syncTransactions() {
        transactionDao.getPending().forEach { tx ->
            api.postTransaction(
                TransactionDto(
                    id             = tx.id,
                    amountCents    = tx.amountCents,
                    type           = tx.type.name,
                    description    = tx.description,
                    categoryId     = tx.categoryId,
                    walletId       = tx.walletId,
                    organizationId = tx.organizationId,
                    userId         = tx.userId,
                    createdAt      = tx.createdAt
                )
            )
            transactionDao.updateSyncStatus(tx.id, SyncStatus.SYNCED)
        }
    }

    private suspend fun syncProducts() {
        productDao.getPending().forEach { p ->
            api.postProduct(
                ProductDto(
                    id             = p.id,
                    name           = p.name,
                    walletId       = p.walletId,
                    organizationId = p.organizationId,
                    categoryId     = p.categoryId,
                    salePriceCents = p.salePriceCents,
                    costPriceCents = p.costPriceCents,
                    currentStock   = p.currentStock
                )
            )
            productDao.updateSyncStatus(p.id, SyncStatus.SYNCED)
        }
    }

    private suspend fun syncSales() {
        saleDao.getPending().forEach { s ->
            api.postSale(
                SaleDto(
                    id                 = s.id,
                    productId          = s.productId,
                    transactionId      = s.transactionId,
                    quantity           = s.quantity,
                    salePriceCentsEach = s.salePriceCentsEach,
                    costPriceCentsEach = s.costPriceCentsEach,
                    walletId           = s.walletId,
                    organizationId     = s.organizationId,
                    createdAt          = s.createdAt
                )
            )
            saleDao.updateSyncStatus(s.id, SyncStatus.SYNCED)
        }
    }

    private suspend fun syncLosses() {
        lossDao.getPending().forEach { l ->
            api.postLoss(
                LossDto(
                    id             = l.id,
                    productId      = l.productId,
                    quantity       = l.quantity,
                    reason         = l.reason,
                    walletId       = l.walletId,
                    organizationId = l.organizationId,
                    createdAt      = l.createdAt
                )
            )
            lossDao.updateSyncStatus(l.id, SyncStatus.SYNCED)
        }
    }

    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "kash_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
