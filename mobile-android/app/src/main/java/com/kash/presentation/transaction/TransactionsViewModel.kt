package com.kash.presentation.transaction

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
class TransactionsViewModel @Inject constructor(
    private val api: KashApiService
) : ViewModel() {

    enum class Filter { ALL, INFLOW, OUTFLOW }

    data class State(
        val loading: Boolean = true,
        val all: List<TransactionResponseDto> = emptyList(),
        val filter: Filter = Filter.ALL,
        val error: String? = null
    ) {
        val visible: List<TransactionResponseDto> get() = when (filter) {
            Filter.ALL     -> all
            Filter.INFLOW  -> all.filter { it.type == "INFLOW" }
            Filter.OUTFLOW -> all.filter { it.type == "OUTFLOW" }
        }
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { api.getTransactions(limit = 200) }
                .onSuccess { txs -> _state.update { it.copy(loading = false, all = txs) } }
                .onFailure { e  -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun setFilter(f: Filter) = _state.update { it.copy(filter = f) }
}
