package com.kash.presentation.spaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.remote.api.KashApiService
import com.kash.data.remote.dto.CreateWalletRequest
import com.kash.data.remote.dto.RenameWalletRequest
import com.kash.data.remote.dto.WalletDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpacesViewModel @Inject constructor(
    private val api: KashApiService
) : ViewModel() {

    data class State(
        val loading: Boolean = true,
        val wallets: List<WalletDto> = emptyList(),
        val error: String? = null,
        val feedback: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { api.getWallets() }
                .onSuccess { ws -> _state.update { it.copy(loading = false, wallets = ws) } }
                .onFailure { e  -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun create(name: String) {
        viewModelScope.launch {
            runCatching { api.createWallet(CreateWalletRequest(name)) }
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(feedback = e.message) } }
        }
    }

    fun rename(id: String, name: String) {
        viewModelScope.launch {
            runCatching { api.renameWallet(id, RenameWalletRequest(name)) }
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(feedback = e.message) } }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            runCatching { api.deleteWallet(id) }
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(feedback = e.message) } }
        }
    }

    fun clearFeedback() = _state.update { it.copy(feedback = null) }
}
