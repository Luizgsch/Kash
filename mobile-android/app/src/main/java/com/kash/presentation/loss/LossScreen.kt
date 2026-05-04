package com.kash.presentation.loss

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.domain.model.Product
import com.kash.presentation.theme.KashColors
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun LossScreen(viewModel: LossViewModel = hiltViewModel()) {
    val products     by viewModel.products.collectAsStateWithLifecycle()
    val recentLosses by viewModel.recentLosses.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = KashColors.Background,
        topBar = { LossTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = viewModel::openSheet,
                containerColor = KashColors.LossRed,
                contentColor   = Color.White,
                shape          = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Registrar perda")
            }
        }
    ) { padding ->
        if (recentLosses.isEmpty()) {
            EmptyLosses(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(padding),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentLosses, key = { it.id }) { loss ->
                    LossRow(loss)
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }

    if (viewModel.showSheet) {
        RegisterLossSheet(
            products    = products,
            selected    = viewModel.selectedProduct,
            quantity    = viewModel.quantity,
            reason      = viewModel.reason,
            isSaving    = viewModel.isSaving,
            error       = viewModel.saveError,
            onDismiss   = viewModel::dismissSheet,
            onSelect    = viewModel::selectProduct,
            onIncrement = viewModel::increment,
            onDecrement = viewModel::decrement,
            onReason    = viewModel::updateReason,
            onConfirm   = viewModel::submit
        )
    }
}

// ── Top Bar ────────────────────────────────────────────────────────────────────

@Composable
private fun LossTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text("Perdas", style = MaterialTheme.typography.headlineLarge, color = KashColors.OnSurface)
    }
}

// ── Loss Row ───────────────────────────────────────────────────────────────────

@Composable
private fun LossRow(tx: ProductTransactionEntity) {
    val costCents = tx.quantity * tx.unitCostCents
    val dateLabel = remember(tx.createdAt) {
        SimpleDateFormat("dd/MM  HH:mm", Locale.getDefault()).format(Date(tx.createdAt))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(KashColors.Surface)
            .border(1.dp, KashColors.Border, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(KashColors.LossRed.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.DeleteOutline, null, tint = KashColors.LossRed, modifier = Modifier.size(18.dp))
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                tx.productName,
                style    = MaterialTheme.typography.bodyLarge,
                color    = KashColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val subtitle = buildString {
                append("${tx.quantity}x  •  $dateLabel")
                if (tx.reason.isNotBlank()) append("  •  ${tx.reason}")
            }
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "- ${formatCents(costCents)}",
                style = MaterialTheme.typography.titleMedium,
                color = KashColors.LossRed
            )
            if (!tx.synced) {
                Icon(Icons.Outlined.CloudOff, null, tint = KashColors.OnSurfaceFaint, modifier = Modifier.size(12.dp))
            }
        }
    }
}

// ── Bottom Sheet: Registrar Perda ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterLossSheet(
    products: List<Product>,
    selected: Product?,
    quantity: Int,
    reason: String,
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSelect: (Product) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReason: (String) -> Unit,
    onConfirm: () -> Unit
) {
    var dropdownOpen by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = KashColors.Surface,
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
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

            // Produto
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("PRODUTO", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
                ExposedDropdownMenuBox(expanded = dropdownOpen, onExpandedChange = { dropdownOpen = it }) {
                    OutlinedTextField(
                        value         = selected?.name ?: "Selecionar produto",
                        onValueChange = {},
                        readOnly      = true,
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownOpen) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor(),
                        shape         = RoundedCornerShape(10.dp),
                        colors        = lossFieldColors()
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
                                    onClick = { onSelect(p); dropdownOpen = false }
                                )
                            }
                        }
                    }
                }
            }

            // Quantidade
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("QUANTIDADE", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick  = onDecrement,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(KashColors.SurfaceVariant).size(44.dp)
                    ) {
                        Icon(Icons.Outlined.Remove, "Diminuir", tint = KashColors.OnSurface)
                    }
                    Text(
                        "$quantity",
                        style    = MaterialTheme.typography.headlineMedium,
                        color    = KashColors.OnSurface,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick  = onIncrement,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(KashColors.SurfaceVariant).size(44.dp)
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
                    onValueChange = onReason,
                    placeholder   = { Text("Ex: estragou, venceu...", color = KashColors.OnSurfaceFaint) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(10.dp),
                    colors        = lossFieldColors(),
                    singleLine    = true
                )
            }

            // Custo da perda
            selected?.let { p ->
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

            if (error != null) {
                Text(error, style = MaterialTheme.typography.bodyMedium, color = KashColors.LossRed)
            }

            Button(
                onClick  = onConfirm,
                enabled  = selected != null && !isSaving,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = KashColors.LossRed,
                    disabledContainerColor = KashColors.LossRed.copy(alpha = 0.4f),
                    contentColor           = Color.White
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Confirmar Perda", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

// ── Empty State ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyLosses(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.CheckCircleOutline, null, tint = KashColors.ProfitGreen, modifier = Modifier.size(48.dp))
            Text("Nenhuma perda registrada", style = MaterialTheme.typography.bodyLarge, color = KashColors.OnSurfaceMuted)
            Text("Toque em + para registrar uma baixa", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceFaint)
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun lossFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = KashColors.LossRed,
    unfocusedBorderColor    = KashColors.Border,
    focusedLabelColor       = KashColors.LossRed,
    unfocusedLabelColor     = KashColors.OnSurfaceMuted,
    focusedTextColor        = KashColors.OnSurface,
    unfocusedTextColor      = KashColors.OnSurface,
    cursorColor             = KashColors.LossRed,
    focusedContainerColor   = KashColors.SurfaceVariant,
    unfocusedContainerColor = KashColors.SurfaceVariant
)

private fun formatCents(cents: Long): String {
    val r = abs(cents) / 100
    val c = abs(cents) % 100
    return "R\$ $r,${"$c".padStart(2, '0')}"
}
