package com.kash.presentation.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.local.UserPreferences
import com.kash.data.remote.api.KashApiService
import com.kash.data.remote.dto.TransactionDto
import com.kash.data.remote.dto.WalletDto
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
class AddTransactionViewModel @Inject constructor(
    private val api: KashApiService,
    private val prefs: UserPreferences
) : ViewModel() {

    data class State(
        val wallets: List<WalletDto> = emptyList(),
        val selectedWalletId: String = "",
        val selectedCategoryId: String? = null,
        val type: String = "INFLOW",
        val digits: String = "",
        val description: String = "",
        val saving: Boolean = false,
        val saved: Boolean = false,
        val error: String? = null
    ) {
        val amountCents: Long get() = digits.toLongOrNull() ?: 0L
        val selectedWallet: WalletDto? get() = wallets.find { it.id == selectedWalletId }
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init { loadWallets() }

    private fun loadWallets() {
        viewModelScope.launch {
            runCatching { api.getWallets() }.onSuccess { wallets ->
                val session = prefs.session.first()
                val defaultId = session?.activeWalletId?.takeIf { it.isNotBlank() }
                    ?: wallets.firstOrNull()?.id ?: ""
                _state.update { it.copy(wallets = wallets, selectedWalletId = defaultId) }
            }
        }
    }

    fun setType(t: String)           = _state.update { it.copy(type = t, selectedCategoryId = null) }
    fun setDigits(d: String)         = _state.update { it.copy(digits = d.filter(Char::isDigit).take(10)) }
    fun setDescription(d: String)    = _state.update { it.copy(description = d) }
    fun setWallet(id: String)        = _state.update { it.copy(selectedWalletId = id, selectedCategoryId = null) }
    fun setCategory(id: String?)     = _state.update { it.copy(selectedCategoryId = id) }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.amountCents <= 0 || s.selectedWalletId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            val session = prefs.session.first() ?: return@launch
            runCatching {
                api.postTransaction(
                    TransactionDto(
                        id             = UUID.randomUUID().toString(),
                        amountCents    = s.amountCents,
                        type           = s.type,
                        description    = s.description.trim(),
                        categoryId     = s.selectedCategoryId,
                        walletId       = s.selectedWalletId,
                        organizationId = session.orgId,
                        userId         = session.userId,
                        createdAt      = System.currentTimeMillis()
                    )
                )
            }.onSuccess {
                _state.update { it.copy(saving = false, saved = true, digits = "", description = "", selectedCategoryId = null) }
                onDone()
            }.onFailure { e ->
                _state.update { it.copy(saving = false, error = e.message) }
            }
        }
    }
}
