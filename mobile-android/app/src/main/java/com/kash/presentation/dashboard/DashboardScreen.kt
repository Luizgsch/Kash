package com.kash.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.remote.dto.TransactionResponseDto
import com.kash.presentation.theme.KashColors
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier            = Modifier.fillMaxSize().background(KashColors.Background),
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text  = "Dashboard",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = KashColors.OnSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "Visão geral da sua organização",
                style = MaterialTheme.typography.bodyMedium,
                color = KashColors.OnSurfaceMuted
            )
        }

        item { BalanceCard(cents = state.balanceCents, loading = state.loading) }

        item {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(Modifier.weight(1f), "Entrada",  state.incomeCents,  positive = true,  loading = state.loading)
                MetricCard(Modifier.weight(1f), "Saída",    state.expenseCents, positive = false, loading = state.loading)
            }
        }

        if (!state.loading && state.recent.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                Text("Últimas movimentações", style = MaterialTheme.typography.titleSmall, color = KashColors.OnSurfaceMuted)
            }
            item {
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KashColors.Surface)
                ) {
                    state.recent.forEachIndexed { index, tx ->
                        TransactionRow(tx)
                        if (index < state.recent.lastIndex)
                            HorizontalDivider(color = KashColors.Border, thickness = 0.5.dp)
                    }
                }
            }
        }

        if (state.error != null) {
            item {
                Text(state.error!!, style = MaterialTheme.typography.bodySmall, color = KashColors.LossRed)
                TextButton(onClick = { viewModel.load() }) {
                    Text("Tentar novamente", color = KashColors.Accent)
                }
            }
        }

        item { Spacer(Modifier.navigationBarsPadding()) }
    }
}

@Composable
private fun BalanceCard(cents: Long, loading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = KashColors.Surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier.size(40.dp).clip(CircleShape).background(KashColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.AccountBalanceWallet, null, tint = KashColors.OnSurfaceMuted, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Saldo Atual", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceMuted)
            }
            Spacer(Modifier.height(12.dp))
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = KashColors.Accent, strokeWidth = 2.dp)
            } else {
                Text(
                    text  = formatCurrency(cents),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = KashColors.OnSurface
                )
            }
        }
    }
}

@Composable
private fun MetricCard(modifier: Modifier, label: String, cents: Long, positive: Boolean, loading: Boolean) {
    val color  = if (positive) KashColors.ProfitGreen else KashColors.LossRed
    val icon   = if (positive) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown
    val bgTint = if (positive) Color(0x1A4CAF50) else Color(0x1AEF5350)

    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = KashColors.Surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier.size(32.dp).clip(CircleShape).background(bgTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodySmall, color = KashColors.OnSurfaceMuted)
            }
            Spacer(Modifier.height(8.dp))
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = color, strokeWidth = 2.dp)
            } else {
                Text(
                    text  = formatCurrency(cents),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(tx: TransactionResponseDto) {
    val isInflow = tx.type == "INFLOW"
    val emoji    = categoryEmoji(tx.categoryName)
    val dateFmt  = remember { SimpleDateFormat("d MMM", Locale("pt", "BR")) }
    val dateStr  = remember(tx.createdAt) { dateFmt.format(Date(tx.createdAt)) }

    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
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
                    text     = tx.description.ifBlank { tx.categoryName },
                    style    = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color    = KashColors.OnSurface,
                    maxLines = 1
                )
                Text(
                    text  = "${tx.categoryName} · $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = KashColors.OnSurfaceMuted
                )
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

internal fun formatCurrency(cents: Long): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(cents / 100.0)

internal fun categoryEmoji(name: String): String {
    val n = name.trim().lowercase()
    return when {
        n.contains("alimenta") || n.contains("comida") || n.contains("mercado") -> "🛒"
        n.contains("transport") || n.contains("combustív") || n.contains("uber") -> "🚗"
        n.contains("saúde") || n.contains("médic") || n.contains("farmácia")     -> "💊"
        n.contains("lazer") || n.contains("entrete")                              -> "🎮"
        n.contains("educação") || n.contains("escola") || n.contains("curso")    -> "📚"
        n.contains("vestuário") || n.contains("roupa") || n.contains("loja")     -> "👗"
        n.contains("moradia") || n.contains("aluguel") || n.contains("casa")     -> "🏠"
        n.contains("salário") || n.contains("renda") || n.contains("receita")    -> "💰"
        else                                                                       -> "💳"
    }
}
