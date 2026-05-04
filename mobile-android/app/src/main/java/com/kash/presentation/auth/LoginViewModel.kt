package com.kash.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase
) : ViewModel() {

    var email    by mutableStateOf("") ; private set
    var password by mutableStateOf("") ; private set
    var name     by mutableStateOf("") ; private set

    sealed interface UiState {
        data object Idle    : UiState
        data object Loading : UiState
        data object Success : UiState
        data class  Error(val message: String) : UiState
    }

    var uiState by mutableStateOf<UiState>(UiState.Idle) ; private set

    fun onEmail(v: String)    { email    = v ; clearError() }
    fun onPassword(v: String) { password = v ; clearError() }
    fun onName(v: String)     { name     = v ; clearError() }

    fun submit() {
        if (email.isBlank() || password.isBlank()) {
            uiState = UiState.Error("Preencha e-mail e senha")
            return
        }
        viewModelScope.launch {
            uiState = UiState.Loading
            val result = login(email.trim(), password)
            uiState = if (result.isSuccess) UiState.Success
                      else UiState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            val result = login.withGoogle(idToken)
            uiState = if (result.isSuccess) UiState.Success
                      else UiState.Error(result.exceptionOrNull()?.message ?: "Erro com Google")
        }
    }

    fun register() {
        if (email.isBlank() || password.isBlank()) {
            uiState = UiState.Error("Preencha e-mail e senha")
            return
        }
        viewModelScope.launch {
            uiState = UiState.Loading
            val result = login.register(email.trim(), password, name.trim().ifBlank { email.substringBefore("@") })
            uiState = if (result.isSuccess) UiState.Success
                      else UiState.Error(result.exceptionOrNull()?.message ?: "Erro ao registrar")
        }
    }

    private fun clearError() { if (uiState is UiState.Error) uiState = UiState.Idle }
}
