package com.kash.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.remote.api.KashApiService
import com.kash.data.remote.dto.ProfileDto
import com.kash.data.remote.dto.UpdateNameRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: KashApiService
) : ViewModel() {

    data class State(
        val loading: Boolean = true,
        val profile: ProfileDto? = null,
        val saving: Boolean = false,
        val feedback: String? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { api.getProfile() }
                .onSuccess { p -> _state.update { it.copy(loading = false, profile = p) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun updateName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            runCatching { api.updateProfile(UpdateNameRequest(name.trim())) }
                .onSuccess { p -> _state.update { it.copy(saving = false, profile = p, feedback = "Nome atualizado.") } }
                .onFailure { e -> _state.update { it.copy(saving = false, feedback = e.message) } }
        }
    }

    fun clearFeedback() = _state.update { it.copy(feedback = null) }
}
