package com.kash.presentation.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.data.local.entity.ProductTransactionType
import com.kash.domain.model.Product
import com.kash.presentation.theme.KashColors
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// ── Ponto de entrada ──────────────────────────────────────────────────────────

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val profitState by viewModel.profitState.collectAsStateWithLifecycle()
    val recent      by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val products    by viewModel.products.collectAsStateWithLifecycle()

    var showLossSheet    by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = KashColors.Background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick        = { showLossSheet = true },
                containerColor = KashColors.SurfaceVariant,
                contentColor   = KashColors.LossRed,
                icon           = { Icon(Icons.Outlined.RemoveCircleOutline, contentDescription = null) },
                text           = { Text("Registrar Perda", style = MaterialTheme.typography.labelMedium) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(padding),
            contentPadding      = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { DashboardHeader(onLogoutClick = { showLogoutDialog = true }) }
            item { ProfitHeroCard(profitState) }
            item { MetricsRow(profitState) }
            if (recent.isNotEmpty()) {
                item { SectionLabel("Últimas movimentações") }
                items(recent, key = { it.id }) { tx -> TransactionRow(tx) }
                item {
                    TextButton(
                        onClick  = onNavigateToHistory,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                    ) {
                        Text(
                            "Ver histórico completo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KashColors.Accent
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Outlined.ArrowForward, null, tint = KashColors.Accent, modifier = Modifier.size(14.dp))
                    }
                }
            }
            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor   = KashColors.Surface,
            title = { Text("Sair da conta?", color = KashColors.OnSurface) },
            text  = {
                Text(
                    "Você precisará fazer login novamente.",
                    color = KashColors.OnSurfaceMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Sair", color = KashColors.LossRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = KashColors.OnSurfaceMuted)
                }
            }
        )
    }

    if (showLossSheet) {
        RegisterLossSheet(
            products  = products,
            onDismiss = { showLossSheet = false },
            onConfirm = { product, qty, reason ->
                viewModel.registerLoss(product, qty, reason)
                showLossSheet = false
            }
        )
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader(onLogoutClick: () -> Unit) {
    val dateLabel = remember {
        SimpleDateFormat("EEEE, d MMM", Locale("pt", "BR"))
            .format(Date())
            .replaceFirstChar { it.uppercase() }
    }
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Text("Kash", style = MaterialTheme.typography.headlineLarge, color = KashColors.OnSurface)
            Text(dateLabel, style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceMuted)
        }
        IconButton(onClick = onLogoutClick) {
            Icon(
                imageVector        = Icons.Outlined.AccountCircle,
                contentDescription = "Sair",
                tint               = KashColors.OnSurfaceFaint,
                modifier           = Modifier.size(28.dp)
            )
        }
    }
}

// ── Card Hero: Resultado do Dia ───────────────────────────────────────────────

@Composable
private fun ProfitHeroCard(state: DashboardViewModel.ProfitState) {
    val cardColor by animateColorAsState(
        targetValue = when (state) {
            is DashboardViewModel.ProfitState.Profit    -> Color(0xFF0A1A0A)
            is DashboardViewModel.ProfitState.Loss      -> Color(0xFF1A0A0A)
            else                                         -> KashColors.Surface
        },
        animationSpec = tween(600),
        label = "heroCardBg"
    )
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            is DashboardViewModel.ProfitState.Profit    -> KashColors.ProfitGreen.copy(alpha = 0.25f)
            is DashboardViewModel.ProfitState.Loss      -> KashColors.LossRed.copy(alpha = 0.25f)
            else                                         -> KashColors.Border
        },
        animationSpec = tween(600),
        label = "heroCardBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        when (state) {
            DashboardViewModel.ProfitState.Loading ->
                CircularProgressIndicator(
                    modifier    = Modifier.align(Alignment.Center).size(24.dp),
                    color       = KashColors.Accent,
                    strokeWidth = 1.5.dp
                )
            is DashboardViewModel.ProfitState.Profit ->
                HeroContent("Lucro do Dia", state.netCents, KashColors.ProfitGreen, Icons.Outlined.TrendingUp)
            is DashboardViewModel.ProfitState.Loss ->
                HeroContent("Prejuízo do Dia", state.netCents, KashColors.LossRed, Icons.Outlined.TrendingDown)
            is DashboardViewModel.ProfitState.BreakEven ->
                HeroContent("Equilíbrio", 0L, KashColors.OnSurfaceMuted, Icons.Outlined.HorizontalRule)
        }
    }
}

@Composable
private fun HeroContent(label: String, valueCents: Long, color: Color, icon: ImageVector) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = color)
        }
        Text(
            text  = formatCents(valueCents),
            style = MaterialTheme.typography.displayLarge,
            color = KashColors.OnSurface
        )
    }
}

// ── Métricas secundárias ──────────────────────────────────────────────────────

@Composable
private fun MetricsRow(state: DashboardViewModel.ProfitState) {
    val revenue = when (state) {
        is DashboardViewModel.ProfitState.Profit    -> state.revenueCents
        is DashboardViewModel.ProfitState.Loss      -> state.revenueCents
        is DashboardViewModel.ProfitState.BreakEven -> state.revenueCents
        else                                         -> 0L
    }
    val cogs = when (state) {
        is DashboardViewModel.ProfitState.Profit    -> state.cogsCents
        is DashboardViewModel.ProfitState.Loss      -> state.cogsCents
        else                                         -> 0L
    }
    val losses = when (state) {
        is DashboardViewModel.ProfitState.Profit    -> state.lossCents
        is DashboardViewModel.ProfitState.Loss      -> state.lossCents
        is DashboardViewModel.ProfitState.BreakEven -> state.lossCents
        else                                         -> 0L
    }

    Column(
        modifier            = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallMetricCard("Receita",        formatCents(revenue), KashColors.ProfitGreen,   Modifier.weight(1f))
            SmallMetricCard("Custo de Vendas", formatCents(cogs),   KashColors.OnSurfaceMuted, Modifier.weight(1f))
        }
        if (losses > 0) LossIndicatorRow(losses)
    }
}

@Composable
private fun SmallMetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(KashColors.Surface)
            .border(1.dp, KashColors.Border, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceFaint)
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color)
    }
}

@Composable
private fun LossIndicatorRow(lossCents: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A0A0A))
            .border(1.dp, KashColors.LossRed.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.WarningAmber, null, tint = KashColors.LossRed, modifier = Modifier.size(16.dp))
            Text("Perdas do dia", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceMuted)
        }
        Text("- ${formatCents(lossCents)}", style = MaterialTheme.typography.titleMedium, color = KashColors.LossRed)
    }
}

// ── Transações recentes ───────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text     = text.uppercase(),
        style    = MaterialTheme.typography.labelMedium,
        color    = KashColors.OnSurfaceMuted,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
    )
}

@Composable
private fun TransactionRow(tx: ProductTransactionEntity) {
    val isSale     = tx.type == ProductTransactionType.SALE
    val color      = if (isSale) KashColors.ProfitGreen else KashColors.LossRed
    val valueCents = if (isSale) tx.quantity * tx.unitPriceCents else tx.quantity * tx.unitCostCents
    val prefix     = if (isSale) "+" else "-"
    val timeLabel  = remember(tx.createdAt) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(tx.createdAt))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(KashColors.Surface)
            .border(1.dp, KashColors.Border, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector        = if (isSale) Icons.Outlined.ShoppingCart else Icons.Outlined.DeleteOutline,
            contentDescription = null,
            tint               = color,
            modifier           = Modifier.size(18.dp)
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                tx.productName,
                style    = MaterialTheme.typography.bodyLarge,
                color    = KashColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${tx.quantity}x  •  $timeLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = KashColors.OnSurfaceMuted
            )
        }
        Text("$prefix ${formatCents(valueCents)}", style = MaterialTheme.typography.titleMedium, color = color)
        if (!tx.synced) {
            Icon(
                Icons.Outlined.CloudOff,
                contentDescription = "Pendente de sync",
                tint               = KashColors.OnSurfaceFaint,
                modifier           = Modifier.size(14.dp)
            )
        }
    }
}

// ── Bottom Sheet: Registrar Perda ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterLossSheet(
    products: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (Product, Int, String) -> Unit
) {
    var selectedProduct by remember { mutableStateOf<Product?>(products.firstOrNull()) }
    var quantity        by remember { mutableIntStateOf(1) }
    var reason          by remember { mutableStateOf("") }
    var dropdownOpen    by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = KashColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(KashColors.Border)
            )
        }
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Registrar Perda", style = MaterialTheme.typography.headlineMedium, color = KashColors.OnSurface)

            // Seletor de produto
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("PRODUTO", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
                ExposedDropdownMenuBox(
                    expanded         = dropdownOpen,
                    onExpandedChange = { dropdownOpen = it }
                ) {
                    OutlinedTextField(
                        value         = selectedProduct?.name ?: "Selecionar produto",
                        onValueChange = {},
                        readOnly      = true,
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownOpen) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor(),
                        shape         = RoundedCornerShape(10.dp),
                        colors        = kashTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded         = dropdownOpen,
                        onDismissRequest = { dropdownOpen = false },
                        containerColor   = KashColors.SurfaceVariant
                    ) {
                        if (products.isEmpty()) {
                            DropdownMenuItem(
                                text    = { Text("Nenhum produto cadastrado", color = KashColors.OnSurfaceMuted) },
                                onClick = { dropdownOpen = false }
                            )
                        } else {
                            products.forEach { p ->
                                DropdownMenuItem(
                                    text    = { Text(p.name, color = KashColors.OnSurface) },
                                    onClick = { selectedProduct = p; dropdownOpen = false }
                                )
                            }
                        }
                    }
                }
            }

            // Quantidade com stepper
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("QUANTIDADE", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick  = { if (quantity > 1) quantity-- },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KashColors.SurfaceVariant)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Outlined.Remove, "Diminuir", tint = KashColors.OnSurface)
                    }
                    Text(
                        text      = "$quantity",
                        style     = MaterialTheme.typography.headlineMedium,
                        color     = KashColors.OnSurface,
                        modifier  = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick  = { quantity++ },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KashColors.SurfaceVariant)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Outlined.Add, "Aumentar", tint = KashColors.OnSurface)
                    }
                }
            }

            // Motivo
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("MOTIVO (OPCIONAL)", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
                OutlinedTextField(
                    value         = reason,
                    onValueChange = { reason = it },
                    placeholder   = { Text("Ex: estragou, venceu...", color = KashColors.OnSurfaceFaint) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(10.dp),
                    colors        = kashTextFieldColors(),
                    singleLine    = true
                )
            }

            // Custo da perda
            selectedProduct?.let { p ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1A0A0A))
                        .border(1.dp, KashColors.LossRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Custo desta perda", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceMuted)
                    Text("- ${formatCents(p.costPriceCents * quantity)}", style = MaterialTheme.typography.titleMedium, color = KashColors.LossRed)
                }
            }

            // Confirmar
            Button(
                onClick  = { selectedProduct?.let { onConfirm(it, quantity, reason) } },
                enabled  = selectedProduct != null,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = KashColors.LossRed,
                    contentColor   = Color.White
                )
            ) {
                Text("Confirmar Perda", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun kashTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = KashColors.Accent,
    unfocusedBorderColor = KashColors.Border,
    focusedTextColor     = KashColors.OnSurface,
    unfocusedTextColor   = KashColors.OnSurface,
    cursorColor          = KashColors.Accent
)

private fun formatCents(cents: Long): String {
    val reais    = abs(cents) / 100
    val centavos = abs(cents) % 100
    return "R\$ $reais,${"$centavos".padStart(2, '0')}"
}
