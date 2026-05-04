package com.kash.presentation.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.local.UserPreferences
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.data.local.entity.ProductTransactionType
import com.kash.domain.repository.DailyProfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.Calendar
import javax.inject.Inject

enum class HistoryPeriod(val label: String) {
    TODAY("Hoje"), WEEK("Semana"), MONTH("Mês")
}

enum class HistoryFilter(val label: String) {
    ALL("Todos"), SALES("Vendas"), LOSSES("Perdas")
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val profitRepo: DailyProfitRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _period = MutableStateFlow(HistoryPeriod.TODAY)
    private val _filter = MutableStateFlow(HistoryFilter.ALL)

    var period by mutableStateOf(HistoryPeriod.TODAY) ; private set
    var filter by mutableStateOf(HistoryFilter.ALL)   ; private set

    fun selectPeriod(p: HistoryPeriod) { period = p; _period.value = p }
    fun selectFilter(f: HistoryFilter) { filter = f; _filter.value = f }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<ProductTransactionEntity>> =
        combine(_period, _filter, prefs.session) { p, f, session -> Triple(p, f, session) }
            .flatMapLatest { (p, f, session) ->
                if (session == null) return@flatMapLatest flowOf(emptyList())
                val (start, end) = periodRange(p)
                val typeFilter = when (f) {
                    HistoryFilter.SALES  -> ProductTransactionType.SALE.name
                    HistoryFilter.LOSSES -> ProductTransactionType.LOSS.name
                    HistoryFilter.ALL    -> null
                }
                profitRepo.watchByPeriodAndType(session.activeWalletId, start, end, typeFilter)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private fun periodRange(p: HistoryPeriod): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return when (p) {
            HistoryPeriod.TODAY -> {
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_MONTH, 1)
                start to cal.timeInMillis
            }
            HistoryPeriod.WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                val start = cal.timeInMillis
                cal.add(Calendar.WEEK_OF_YEAR, 1)
                start to cal.timeInMillis
            }
            HistoryPeriod.MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis
                cal.add(Calendar.MONTH, 1)
                start to cal.timeInMillis
            }
        }
    }
}
