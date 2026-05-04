package com.kash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.domain.model.AuthState
import com.kash.domain.model.UserSession
import com.kash.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepo.watchSession()
        .map { session: UserSession? ->
            if (session != null) AuthState.Authenticated(session)
            else AuthState.Unauthenticated
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Loading)

    fun logout() = viewModelScope.launch { authRepo.logout() }
}
