package com.kash.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.local.UserPreferences
import com.kash.domain.model.ProfitabilityInsights
import com.kash.domain.usecase.GetProfitabilityInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getInsights: GetProfitabilityInsightsUseCase,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _period = MutableStateFlow(Period.TODAY)
    val period: StateFlow<Period> = _period.asStateFlow()

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val insights: ProfitabilityInsights) : UiState
        data class Error(val message: String) : UiState
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState> =
        combine(_period, prefs.session) { period, session -> period to session }
            .flatMapLatest { (period, session) ->
                if (session == null) return@flatMapLatest flowOf(UiState.Error("Sessão expirada"))
                val (start, end) = period.toRange()
                getInsights(
                    walletId = session.activeWalletId,
                    orgId    = session.orgId,
                    startMs  = start,
                    endMs    = end
                )
                    .map<ProfitabilityInsights, UiState> { UiState.Success(it) }
                    .catch { e -> emit(UiState.Error(e.message ?: "Erro ao carregar insights")) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun selectPeriod(p: Period) { _period.value = p }
}
