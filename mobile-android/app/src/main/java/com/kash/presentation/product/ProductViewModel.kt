package com.kash.presentation.product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.local.UserPreferences
import com.kash.domain.model.Product
import com.kash.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepo: ProductRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    sealed interface SheetState {
        data object Hidden : SheetState
        data object Adding : SheetState
        data class  Editing(val product: Product) : SheetState
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> = prefs.session
        .flatMapLatest { session ->
            if (session == null) return@flatMapLatest flowOf(emptyList())
            productRepo.watchByWallet(session.activeWalletId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var sheetState  by mutableStateOf<SheetState>(SheetState.Hidden)  ; private set
    var isSaving    by mutableStateOf(false)                           ; private set
    var saveError   by mutableStateOf<String?>(null)                   ; private set

    fun openAdd()                    { sheetState = SheetState.Adding ; saveError = null }
    fun openEdit(p: Product)         { sheetState = SheetState.Editing(p) ; saveError = null }
    fun dismissSheet()               { sheetState = SheetState.Hidden }

    fun save(
        name: String,
        salePriceCents: Long,
        costPriceCents: Long,
        stock: Int,
        existing: Product? = null
    ) {
        if (name.isBlank()) { saveError = "Nome obrigatório"; return }
        if (salePriceCents <= 0) { saveError = "Preço de venda inválido"; return }

        viewModelScope.launch {
            isSaving = true
            saveError = null
            try {
                val session = prefs.session.firstOrNull()
                    ?: run { saveError = "Sessão expirada"; isSaving = false; return@launch }

                val product = Product(
                    id             = existing?.id ?: UUID.randomUUID().toString(),
                    name           = name.trim(),
                    walletId       = session.activeWalletId,
                    organizationId = session.orgId,
                    categoryId     = existing?.categoryId,
                    salePriceCents = salePriceCents,
                    costPriceCents = costPriceCents,
                    currentStock   = stock
                )

                if (existing == null) productRepo.insert(product)
                else                  productRepo.update(product)

                sheetState = SheetState.Hidden
            } catch (e: Exception) {
                saveError = e.message ?: "Erro ao salvar"
            } finally {
                isSaving = false
            }
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch {
            try { productRepo.delete(product.id) } catch (_: Exception) {}
        }
    }
}
