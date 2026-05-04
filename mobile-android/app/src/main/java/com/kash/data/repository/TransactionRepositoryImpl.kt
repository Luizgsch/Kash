package com.kash.data.repository

import com.kash.data.local.dao.TransactionDao
import com.kash.data.local.entity.TransactionEntity
import com.kash.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {
    override suspend fun insert(transaction: TransactionEntity) = dao.insert(transaction)
    override suspend fun update(transaction: TransactionEntity) = dao.update(transaction)
    override suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
    override fun watchByWallet(walletId: String): Flow<List<TransactionEntity>> =
        dao.watchByWallet(walletId)
}
