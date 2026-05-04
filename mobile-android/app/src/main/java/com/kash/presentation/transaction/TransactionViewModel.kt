package com.kash.presentation.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kash.data.local.UserPreferences
import com.kash.domain.model.Product
import com.kash.domain.repository.DailyProfitRepository
import com.kash.domain.repository.ProductRepository
import com.kash.domain.usecase.AddSaleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val productRepo: ProductRepository,
    private val dailyProfitRepo: DailyProfitRepository,
    private val addSaleUseCase: AddSaleUseCase,
    private val prefs: UserPreferences
) : ViewModel() {

    data class CartItem(val product: Product, val quantity: Int) {
        val subtotalCents: Long get() = product.salePriceCents * quantity
    }

    sealed interface SaleState {
        data object Idle       : SaleState
        data object Processing : SaleState
        data object Success    : SaleState
        data class  Error(val message: String) : SaleState
    }

    private val _cart = MutableStateFlow<Map<String, CartItem>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> = prefs.session
        .flatMapLatest { session ->
            if (session == null) return@flatMapLatest flowOf(emptyList())
            productRepo.watchByWallet(session.activeWalletId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = _cart
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalCents: StateFlow<Long> = _cart
        .map { cart -> cart.values.sumOf { it.subtotalCents } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    var saleState by mutableStateOf<SaleState>(SaleState.Idle) ; private set

    fun cartQty(productId: String): Int = _cart.value[productId]?.quantity ?: 0

    fun addToCart(product: Product) {
        _cart.update { cart ->
            val qty = (cart[product.id]?.quantity ?: 0) + 1
            cart + (product.id to CartItem(product, qty))
        }
    }

    fun decrement(productId: String) {
        _cart.update { cart ->
            val current = cart[productId] ?: return@update cart
            if (current.quantity <= 1) cart - productId
            else cart + (productId to current.copy(quantity = current.quantity - 1))
        }
    }

    fun removeFromCart(productId: String) {
        _cart.update { it - productId }
    }

    fun clearCart() { _cart.value = emptyMap() }

    fun resetState() { saleState = SaleState.Idle }

    fun confirmSale() {
        val items = _cart.value.values.toList()
        if (items.isEmpty()) return

        viewModelScope.launch {
            saleState = SaleState.Processing
            try {
                val session = prefs.session.firstOrNull()
                    ?: run { saleState = SaleState.Error("Sessão expirada"); return@launch }

                items.forEach { item ->
                    dailyProfitRepo.registerSale(item.product, item.quantity)
                    addSaleUseCase(item.product, item.quantity, session.userId)
                }

                _cart.value = emptyMap()
                saleState   = SaleState.Success
            } catch (e: Exception) {
                saleState = SaleState.Error(e.message ?: "Erro ao registrar venda")
            }
        }
    }
}
