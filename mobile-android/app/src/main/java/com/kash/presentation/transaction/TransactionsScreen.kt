package com.kash.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.remote.dto.TransactionResponseDto
import com.kash.presentation.dashboard.categoryEmoji
import com.kash.presentation.dashboard.formatCurrency
import com.kash.presentation.theme.KashColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(viewModel: TransactionsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().background(KashColors.Background)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            Text(
                text  = "Transações",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = KashColors.OnSurface
            )
        }

        // Filter bar
        FilterBar(
            current   = state.filter,
            onSelect  = viewModel::setFilter
        )

        // Content
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KashColors.Accent)
            }
            state.error != null -> Column(
                modifier            = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.error!!, color = KashColors.LossRed, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { viewModel.load() }) { Text("Tentar novamente") }
            }
            state.visible.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma transação encontrada.", color = KashColors.OnSurfaceMuted)
            }
            else -> LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    Card(
                        shape  = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = KashColors.Surface)
                    ) {
                        state.visible.forEachIndexed { index, tx ->
                            TxRow(tx)
                            if (index < state.visible.lastIndex)
                                HorizontalDivider(color = KashColors.Border, thickness = 0.5.dp)
                        }
                    }
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }
}

@Composable
private fun FilterBar(current: TransactionsViewModel.Filter, onSelect: (TransactionsViewModel.Filter) -> Unit) {
    Row(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionsViewModel.Filter.entries.forEach { f ->
            val selected = f == current
            val label = when (f) {
                TransactionsViewModel.Filter.ALL     -> "Todos"
                TransactionsViewModel.Filter.INFLOW  -> "Entrada"
                TransactionsViewModel.Filter.OUTFLOW -> "Saída"
            }
            FilterChip(
                selected = selected,
                onClick  = { onSelect(f) },
                label    = { Text(label, style = MaterialTheme.typography.labelMedium) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor     = KashColors.Accent,
                    selectedLabelColor         = KashColors.Background,
                    containerColor             = KashColors.SurfaceVariant,
                    labelColor                 = KashColors.OnSurfaceMuted
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled        = true,
                    selected       = selected,
                    borderColor    = KashColors.Border,
                    selectedBorderColor = KashColors.Accent
                )
            )
        }
    }
}

@Composable
private fun TxRow(tx: TransactionResponseDto) {
    val isInflow = tx.type == "INFLOW"
    val emoji    = categoryEmoji(tx.categoryName)
    val dateFmt  = remember { SimpleDateFormat("d MMM", Locale("pt", "BR")) }
    val dateStr  = remember(tx.createdAt) { dateFmt.format(Date(tx.createdAt)) }

    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier         = Modifier
                    .size(40.dp).clip(CircleShape)
                    .background(KashColors.SurfaceVariant)
                    .border(1.dp, KashColors.Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = (tx.description ?: "").ifBlank { tx.categoryName },
                    style    = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color    = KashColors.OnSurface,
                    maxLines = 1
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryBadge(tx.categoryName)
                    TypeBadge(isInflow)
                    Text(dateStr, style = MaterialTheme.typography.bodySmall, color = KashColors.OnSurfaceFaint)
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text  = "${if (isInflow) "+ " else "- "}${formatCurrency(tx.amountCents)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isInflow) KashColors.ProfitGreen else KashColors.LossRed
        )
    }
}

@Composable
private fun CategoryBadge(name: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = KashColors.SurfaceVariant,
        tonalElevation = 0.dp
    ) {
        Text(
            text     = name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style    = MaterialTheme.typography.labelSmall,
            color    = KashColors.OnSurfaceMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TypeBadge(isInflow: Boolean) {
    val color  = if (isInflow) KashColors.ProfitGreen else KashColors.LossRed
    val label  = if (isInflow) "Entrada" else "Saída"
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text     = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style    = MaterialTheme.typography.labelSmall,
            color    = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
