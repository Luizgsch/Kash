package com.kash.presentation.loss

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.local.UserPreferences
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.domain.model.Product
import com.kash.domain.repository.DailyProfitRepository
import com.kash.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LossViewModel @Inject constructor(
    private val profitRepo: DailyProfitRepository,
    private val productRepo: ProductRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> = prefs.session
        .flatMapLatest { session ->
            if (session == null) return@flatMapLatest flowOf(emptyList())
            productRepo.watchByWallet(session.activeWalletId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentLosses: StateFlow<List<ProductTransactionEntity>> = prefs.session
        .flatMapLatest { session ->
            if (session == null) return@flatMapLatest flowOf(emptyList())
            profitRepo.watchLosses(session.activeWalletId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var selectedProduct by mutableStateOf<Product?>(null) ; private set
    var quantity        by mutableIntStateOf(1)           ; private set
    var reason          by mutableStateOf("")              ; private set
    var isSaving        by mutableStateOf(false)           ; private set
    var saveError       by mutableStateOf<String?>(null)   ; private set
    var showSheet       by mutableStateOf(false)           ; private set

    fun openSheet()               { showSheet = true; saveError = null }
    fun dismissSheet()            { showSheet = false }
    fun selectProduct(p: Product) { selectedProduct = p }
    fun increment()               { quantity++ }
    fun decrement()               { if (quantity > 1) quantity-- }
    fun updateReason(r: String)   { reason = r }

    fun submit() {
        val product = selectedProduct ?: run { saveError = "Selecione um produto"; return }
        viewModelScope.launch {
            isSaving  = true
            saveError = null
            try {
                profitRepo.registerLoss(product, quantity, reason)
                showSheet = false
                quantity  = 1
                reason    = ""
            } catch (e: Exception) {
                saveError = e.message ?: "Erro ao registrar perda"
            } finally {
                isSaving = false
            }
        }
    }
}
