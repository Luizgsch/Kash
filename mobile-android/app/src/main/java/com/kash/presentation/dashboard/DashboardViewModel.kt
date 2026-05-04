package com.kash.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.local.UserPreferences
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.domain.model.Product
import com.kash.domain.repository.DailyProfitRepository
import com.kash.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val profitRepo: DailyProfitRepository,
    private val productRepo: ProductRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    sealed interface ProfitState {
        data object Loading : ProfitState
        data class Profit(
            val netCents: Long,
            val revenueCents: Long,
            val cogsCents: Long,
            val lossCents: Long
        ) : ProfitState
        data class Loss(
            val netCents: Long,
            val revenueCents: Long,
            val cogsCents: Long,
            val lossCents: Long
        ) : ProfitState
        data class BreakEven(
            val revenueCents: Long,
            val lossCents: Long
        ) : ProfitState
    }

    private fun todayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        return start to cal.timeInMillis
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val profitState: StateFlow<ProfitState> = prefs.session
        .flatMapLatest { session ->
            if (session == null) return@flatMapLatest flowOf(ProfitState.Loading)
            val (start, end) = todayRange()
            profitRepo.watchDailyProfit(session.activeWalletId, start, end)
                .map { r ->
                    when {
                        r.netProfitCents > 0 -> ProfitState.Profit(r.netProfitCents, r.totalRevenueCents, r.totalCOGSCents, r.totalLossCents)
                        r.netProfitCents < 0 -> ProfitState.Loss(r.netProfitCents, r.totalRevenueCents, r.totalCOGSCents, r.totalLossCents)
                        else                 -> ProfitState.BreakEven(r.totalRevenueCents, r.totalLossCents)
                    }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfitState.Loading)

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentTransactions: StateFlow<List<ProductTransactionEntity>> = prefs.session
        .flatMapLatest { session ->
            if (session == null) return@flatMapLatest flowOf(emptyList())
            profitRepo.watchRecent(session.activeWalletId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> = prefs.session
        .flatMapLatest { session ->
            if (session == null) return@flatMapLatest flowOf(emptyList())
            productRepo.watchByWallet(session.activeWalletId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun registerLoss(product: Product, quantity: Int, reason: String) {
        viewModelScope.launch { profitRepo.registerLoss(product, quantity, reason) }
    }

    fun registerSale(product: Product, quantity: Int) {
        viewModelScope.launch { profitRepo.registerSale(product, quantity) }
    }
}
