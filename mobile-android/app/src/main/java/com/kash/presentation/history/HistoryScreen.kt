package com.kash.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.local.entity.ProductTransactionEntity
import com.kash.data.local.entity.ProductTransactionType
import com.kash.presentation.theme.KashColors
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = KashColors.Background,
        topBar = { HistoryTopBar(onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Period filter
            LazyRow(
                contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HistoryPeriod.entries) { p ->
                    FilterChip(
                        selected = viewModel.period == p,
                        onClick  = { viewModel.selectPeriod(p) },
                        label    = { Text(p.label, style = MaterialTheme.typography.labelMedium) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor    = KashColors.Accent,
                            selectedLabelColor        = KashColors.Background,
                            containerColor            = KashColors.Surface,
                            labelColor                = KashColors.OnSurfaceMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled              = true,
                            selected             = viewModel.period == p,
                            borderColor          = KashColors.Border,
                            selectedBorderColor  = KashColors.Accent
                        )
                    )
                }
            }

            // Type filter
            LazyRow(
                contentPadding      = PaddingValues(start = 20.dp, end = 20.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HistoryFilter.entries) { f ->
                    FilterChip(
                        selected = viewModel.filter == f,
                        onClick  = { viewModel.selectFilter(f) },
                        label    = { Text(f.label, style = MaterialTheme.typography.labelMedium) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor   = KashColors.SurfaceVariant,
                            selectedLabelColor       = KashColors.OnSurface,
                            containerColor           = KashColors.Surface,
                            labelColor               = KashColors.OnSurfaceMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled             = true,
                            selected            = viewModel.filter == f,
                            borderColor         = KashColors.Border,
                            selectedBorderColor = KashColors.Accent
                        )
                    )
                }
            }

            HorizontalDivider(color = KashColors.Border, modifier = Modifier.padding(top = 8.dp))

            if (transactions.isEmpty()) {
                EmptyHistory(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier            = Modifier.weight(1f),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        HistoryRow(tx)
                    }
                    item { Spacer(Modifier.navigationBarsPadding()) }
                }
            }
        }
    }
}

// ── Top Bar ────────────────────────────────────────────────────────────────────

@Composable
private fun HistoryTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Voltar", tint = KashColors.OnSurface)
        }
        Text(
            "Histórico",
            style    = MaterialTheme.typography.headlineLarge,
            color    = KashColors.OnSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

// ── History Row ────────────────────────────────────────────────────────────────

@Composable
private fun HistoryRow(tx: ProductTransactionEntity) {
    val isSale     = tx.type == ProductTransactionType.SALE
    val color      = if (isSale) KashColors.ProfitGreen else KashColors.LossRed
    val valueCents = if (isSale) tx.quantity * tx.unitPriceCents else tx.quantity * tx.unitCostCents
    val prefix     = if (isSale) "+" else "-"
    val dateLabel  = remember(tx.createdAt) {
        SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(tx.createdAt))
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
        // Type badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSale) Icons.Outlined.ShoppingCart else Icons.Outlined.DeleteOutline,
                contentDescription = null,
                tint     = color,
                modifier = Modifier.size(18.dp)
            )
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
                if (!isSale && tx.reason.isNotBlank()) append("  •  ${tx.reason}")
            }
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "$prefix ${formatCents(valueCents)}",
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
            if (!tx.synced) {
                Icon(
                    Icons.Outlined.CloudOff,
                    contentDescription = "Pendente de sync",
                    tint     = KashColors.OnSurfaceFaint,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// ── Empty State ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.History, null, tint = KashColors.OnSurfaceFaint, modifier = Modifier.size(48.dp))
            Text("Sem movimentações", style = MaterialTheme.typography.bodyLarge, color = KashColors.OnSurfaceMuted)
            Text("Nenhuma transação no período selecionado", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceFaint)
        }
    }
}

// ── Helper ─────────────────────────────────────────────────────────────────────

private fun formatCents(cents: Long): String {
    val r = abs(cents) / 100
    val c = abs(cents) % 100
    return "R\$ $r,${"$c".padStart(2, '0')}"
}
