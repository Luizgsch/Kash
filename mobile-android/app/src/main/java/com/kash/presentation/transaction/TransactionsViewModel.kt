package com.kash.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.remote.api.KashApiService
import com.kash.data.remote.dto.TransactionDto
import com.kash.data.remote.dto.TransactionResponseDto
import com.kash.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val api: KashApiService,
    private val prefs: UserPreferences
) : ViewModel() {

    enum class Filter { ALL, INFLOW, OUTFLOW }

    data class State(
        val loading: Boolean = true,
        val all: List<TransactionResponseDto> = emptyList(),
        val filter: Filter = Filter.ALL,
        val selectedTx: TransactionResponseDto? = null,
        val error: String? = null,
        val feedback: String? = null
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

    fun selectTransaction(tx: TransactionResponseDto) = _state.update { it.copy(selectedTx = tx) }

    fun clearSelected() = _state.update { it.copy(selectedTx = null) }

    fun updateTransaction(tx: TransactionResponseDto, amountCents: Long, description: String, type: String, categoryId: String?) {
        viewModelScope.launch {
            runCatching {
                val session = prefs.session.first() ?: return@launch
                api.updateTransaction(
                    tx.id,
                    TransactionDto(
                        id              = tx.id,
                        amountCents     = amountCents,
                        type            = type,
                        description     = description,
                        categoryId      = categoryId,
                        walletId        = tx.walletId,
                        organizationId  = session.orgId,
                        userId          = session.userId,
                        createdAt       = tx.createdAt
                    )
                )
            }.onSuccess {
                load()
                _state.update { it.copy(selectedTx = null, feedback = "Transação atualizada") }
            }.onFailure { e ->
                _state.update { it.copy(feedback = e.message) }
            }
        }
    }

    fun deleteTransaction(txId: String) {
        viewModelScope.launch {
            runCatching { api.deleteTransaction(txId) }
                .onSuccess {
                    load()
                    _state.update { it.copy(selectedTx = null, feedback = "Transação deletada") }
                }
                .onFailure { e ->
                    _state.update { it.copy(feedback = e.message) }
                }
        }
    }

    fun clearFeedback() = _state.update { it.copy(feedback = null) }
}
