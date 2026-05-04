package com.kash.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.remote.api.KashApiService
import com.kash.data.remote.dto.TransactionResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val api: KashApiService
) : ViewModel() {

    data class State(
        val loading: Boolean = true,
        val balanceCents: Long = 0,
        val incomeCents: Long = 0,
        val expenseCents: Long = 0,
        val recent: List<TransactionResponseDto> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { api.getTransactions(limit = 200) }
                .onSuccess { txs ->
                    val income  = txs.filter { it.type == "INFLOW"  }.sumOf { it.amountCents }
                    val expense = txs.filter { it.type == "OUTFLOW" }.sumOf { it.amountCents }
                    _state.update {
                        it.copy(
                            loading      = false,
                            incomeCents  = income,
                            expenseCents = expense,
                            balanceCents = income - expense,
                            recent       = txs.take(10)
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(loading = false, error = e.message) }
                }
        }
    }
}
