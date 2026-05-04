package com.kash.presentation.transaction

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.domain.model.Product
import com.kash.presentation.theme.KashColors
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun TransactionScreen(viewModel: TransactionViewModel = hiltViewModel()) {
    val products  by viewModel.products.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val total     by viewModel.totalCents.collectAsStateWithLifecycle()
    val state      = viewModel.saleState

    // Auto-reset após sucesso
    LaunchedEffect(state) {
        if (state is TransactionViewModel.SaleState.Success) {
            delay(1_800)
            viewModel.resetState()
        }
    }

    Box(Modifier.fillMaxSize().background(KashColors.Background)) {

        Column(Modifier.fillMaxSize()) {

            // ── Header ──────────────────────────────────────────────────────
            CaixaHeader(itemCount = cartItems.sumOf { it.quantity })

            // ── Grade de produtos ────────────────────────────────────────────
            if (products.isEmpty()) {
                EmptyProducts(modifier = Modifier.weight(1f))
            } else {
                LazyVerticalGrid(
                    columns             = GridCells.Fixed(2),
                    modifier            = Modifier.weight(1f),
                    contentPadding      = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            qty     = viewModel.cartQty(product.id),
                            onAdd   = { viewModel.addToCart(product) },
                            onDec   = { viewModel.decrement(product.id) }
                        )
                    }
                    // Espaço para o cart panel não cobrir último item
                    if (cartItems.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(180.dp)) }
                    }
                }
            }
        }

        // ── Cart panel (flutuante) ────────────────────────────────────────
        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            enter   = slideInVertically { it } + fadeIn(),
            exit    = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CartPanel(
                cartItems  = cartItems,
                totalCents = total,
                state      = state,
                onConfirm  = viewModel::confirmSale,
                onRemove   = { viewModel.removeFromCart(it) }
            )
        }

        // ── Overlay de sucesso ────────────────────────────────────────────
        AnimatedVisibility(
            visible  = state is TransactionViewModel.SaleState.Success,
            enter    = fadeIn(),
            exit     = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            SuccessOverlay()
        }
    }
}

// ── Header ─────────────────────────────────────────────────────────────────────

@Composable
private fun CaixaHeader(itemCount: Int) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text("Caixa", style = MaterialTheme.typography.headlineLarge, color = KashColors.OnSurface)
        if (itemCount > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(KashColors.Accent)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$itemCount ${if (itemCount == 1) "item" else "itens"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = KashColors.Background
                )
            }
        }
    }
}

// ── Product Card ───────────────────────────────────────────────────────────────

@Composable
private fun ProductCard(
    product: Product,
    qty: Int,
    onAdd: () -> Unit,
    onDec: () -> Unit
) {
    val inCart      = qty > 0
    val margin      = if (product.salePriceCents > 0)
        ((product.salePriceCents - product.costPriceCents).toFloat() / product.salePriceCents * 100).toInt()
    else 0
    val borderColor = if (inCart) KashColors.Accent.copy(alpha = 0.6f) else KashColors.Border
    val bgColor     = if (inCart) Color(0xFF1C1A14) else KashColors.Surface

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onAdd)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Nome + estoque
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Text(
                text     = product.name,
                style    = MaterialTheme.typography.bodyLarge,
                color    = KashColors.OnSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (product.currentStock > 0) {
                Text(
                    text  = "${product.currentStock}",
                    style = MaterialTheme.typography.labelMedium,
                    color = KashColors.OnSurfaceFaint
                )
            }
        }

        // Preço de venda
        Text(
            text  = formatCents(product.salePriceCents),
            style = MaterialTheme.typography.titleMedium,
            color = if (inCart) KashColors.Accent else KashColors.OnSurface
        )

        // Margem
        Text(
            text  = "margem $margin%",
            style = MaterialTheme.typography.labelMedium,
            color = KashColors.OnSurfaceFaint
        )

        // Stepper (visível só quando em carrinho)
        if (inCart) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick  = onDec,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(KashColors.SurfaceVariant)
                ) {
                    Icon(Icons.Outlined.Remove, null, tint = KashColors.OnSurface, modifier = Modifier.size(14.dp))
                }
                Text(
                    "$qty",
                    style = MaterialTheme.typography.titleMedium,
                    color = KashColors.Accent
                )
                IconButton(
                    onClick  = onAdd,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(KashColors.SurfaceVariant)
                ) {
                    Icon(Icons.Outlined.Add, null, tint = KashColors.OnSurface, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

// ── Cart Panel ─────────────────────────────────────────────────────────────────

@Composable
private fun CartPanel(
    cartItems: List<TransactionViewModel.CartItem>,
    totalCents: Long,
    state: TransactionViewModel.SaleState,
    onConfirm: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(KashColors.Surface)
            .border(
                width = 1.dp,
                color = KashColors.Border,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 20.dp)
    ) {
        // Drag handle
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 4.dp)
                .clip(CircleShape)
                .background(KashColors.Border)
        )

        Spacer(Modifier.height(12.dp))

        // Itens do carrinho (máx 3 visíveis sem scroll)
        LazyColumn(
            modifier           = Modifier
                .fillMaxWidth()
                .heightIn(max = 140.dp),
            contentPadding     = PaddingValues(horizontal = 16.dp),
            verticalArrangement= Arrangement.spacedBy(6.dp)
        ) {
            items(cartItems, key = { it.product.id }) { item ->
                CartItemRow(item = item, onRemove = { onRemove(item.product.id) })
            }
        }

        Spacer(Modifier.height(12.dp))

        // Divider + total
        HorizontalDivider(color = KashColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(12.dp))

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("TOTAL", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
            Text(
                formatCents(totalCents),
                style = MaterialTheme.typography.headlineMedium,
                color = KashColors.ProfitGreen
            )
        }

        Spacer(Modifier.height(14.dp))

        // Erro
        if (state is TransactionViewModel.SaleState.Error) {
            Text(
                text     = state.message,
                style    = MaterialTheme.typography.bodyMedium,
                color    = KashColors.LossRed,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        // Botão confirmar
        Button(
            onClick  = onConfirm,
            enabled  = state !is TransactionViewModel.SaleState.Processing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            shape  = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = KashColors.ProfitGreen,
                disabledContainerColor = KashColors.ProfitGreen.copy(alpha = 0.4f),
                contentColor           = Color.White
            )
        ) {
            if (state is TransactionViewModel.SaleState.Processing) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    color       = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Outlined.CheckCircleOutline, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Confirmar Venda", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: TransactionViewModel.CartItem,
    onRemove: () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text     = item.product.name,
                style    = MaterialTheme.typography.bodyMedium,
                color    = KashColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text  = "${item.quantity}x ${formatCents(item.product.salePriceCents)}",
                style = MaterialTheme.typography.labelMedium,
                color = KashColors.OnSurfaceMuted
            )
        }
        Text(
            formatCents(item.subtotalCents),
            style = MaterialTheme.typography.bodyMedium,
            color = KashColors.OnSurface
        )
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Outlined.Close, null, tint = KashColors.OnSurfaceFaint, modifier = Modifier.size(14.dp))
        }
    }
}

// ── Success Overlay ─────────────────────────────────────────────────────────────

@Composable
private fun SuccessOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE60A1A0A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Outlined.CheckCircleOutline,
                contentDescription = null,
                tint               = KashColors.ProfitGreen,
                modifier           = Modifier.size(64.dp)
            )
            Text("Venda Registrada!", style = MaterialTheme.typography.headlineMedium, color = KashColors.OnSurface)
            Text("Salva offline • sincronizando...", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceMuted)
        }
    }
}

// ── Empty State ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyProducts(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.Inventory, null, tint = KashColors.OnSurfaceFaint, modifier = Modifier.size(48.dp))
            Text("Nenhum produto cadastrado", style = MaterialTheme.typography.bodyLarge, color = KashColors.OnSurfaceMuted)
            Text("Cadastre produtos na aba Produtos", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceFaint)
        }
    }
}

// ── Helper ─────────────────────────────────────────────────────────────────────

private fun formatCents(cents: Long): String {
    val r = abs(cents) / 100
    val c = abs(cents) % 100
    return "R\$ $r,${"$c".padStart(2, '0')}"
}
