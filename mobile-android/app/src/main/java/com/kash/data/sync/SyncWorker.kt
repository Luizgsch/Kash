package com.kash.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.kash.data.local.dao.TransactionDao
import com.kash.data.local.entity.SyncStatus
import com.kash.data.remote.api.KashApiService
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
    private val api: KashApiService
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            syncTransactions()
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
