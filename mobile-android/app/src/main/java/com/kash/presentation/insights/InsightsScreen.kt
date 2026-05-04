package com.kash.presentation.insights

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.domain.model.ProductProfitability
import com.kash.domain.model.ProfitabilityInsights
import com.kash.presentation.theme.KashColors
import kotlin.math.abs

@Composable
fun InsightsScreen(viewModel: InsightsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val period  by viewModel.period.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = KashColors.Background,
        topBar = { InsightsTopBar() }
    ) { padding ->
        when (val state = uiState) {
            is InsightsViewModel.UiState.Loading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KashColors.Accent, strokeWidth = 1.5.dp)
                }
            is InsightsViewModel.UiState.Error ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = KashColors.LossRed, style = MaterialTheme.typography.bodyMedium)
                }
            is InsightsViewModel.UiState.Success ->
                InsightsContent(
                    insights = state.insights,
                    period   = period,
                    onPeriod = viewModel::selectPeriod,
                    modifier = Modifier.padding(padding)
                )
        }
    }
}

@Composable
private fun InsightsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text  = "Insights",
            style = MaterialTheme.typography.headlineLarge,
            color = KashColors.OnSurface
        )
    }
}

@Composable
private fun InsightsContent(
    insights: ProfitabilityInsights,
    period: Period,
    onPeriod: (Period) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier            = modifier.fillMaxSize(),
        contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { PeriodChipRow(selected = period, onSelect = onPeriod) }
        item { SummaryRow(netProfitCents = insights.totalNetProfitCents, totalLossCents = insights.totalLossCents) }
        item { SectionHeader("Matriz de Lucratividade") }
        item { ProfitabilityMatrix(insights.starProducts) }
        item { SectionHeader("Produtos Estrela") }
        items(insights.starProducts, key = { it.productId }) { product ->
            ProductRankCard(
                product     = product,
                rank        = insights.starProducts.indexOf(product) + 1,
                metricLabel = "lucro líquido",
                metricValue = formatCents(product.netProfitCents),
                metricColor = KashColors.ProfitGreen
            )
        }
        item { SectionHeader("Maior Giro") }
        items(insights.volumeProducts, key = { "vol_${it.productId}" }) { product ->
            ProductRankCard(
                product     = product,
                rank        = insights.volumeProducts.indexOf(product) + 1,
                metricLabel = "unidades vendidas",
                metricValue = "${product.totalQuantitySold}x",
                metricColor = KashColors.Accent
            )
        }
        item { Spacer(Modifier.navigationBarsPadding()) }
    }
}

@Composable
private fun PeriodChipRow(selected: Period, onSelect: (Period) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Period.entries.forEach { p ->
            FilterChip(
                selected = p == selected,
                onClick  = { onSelect(p) },
                label    = { Text(p.label(), style = MaterialTheme.typography.labelMedium) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = KashColors.Accent,
                    selectedLabelColor     = KashColors.Background,
                    containerColor         = KashColors.SurfaceVariant,
                    labelColor             = KashColors.OnSurfaceMuted
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled             = true,
                    selected            = p == selected,
                    borderColor         = KashColors.Border,
                    selectedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun SummaryRow(netProfitCents: Long, totalLossCents: Long) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            label    = "Lucro Líquido",
            value    = formatCents(netProfitCents),
            color    = if (netProfitCents >= 0) KashColors.ProfitGreen else KashColors.LossRed,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label    = "Perdas",
            value    = formatCents(totalLossCents),
            color    = KashColors.LossRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(KashColors.Surface)
            .border(1.dp, KashColors.Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color)
    }
}

@Composable
private fun ProfitabilityMatrix(products: List<ProductProfitability>) {
    if (products.isEmpty()) return
    val maxProfit = products.maxOf { it.netProfitCents }.coerceAtLeast(1L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KashColors.Surface)
            .border(1.dp, KashColors.Border, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        products.forEach { p ->
            val ratio = (p.netProfitCents.toFloat() / maxProfit).coerceIn(0f, 1f)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(p.productName, style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurface)
                    Text(
                        text  = formatCents(p.netProfitCents),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (p.netProfitCents >= 0) KashColors.ProfitGreen else KashColors.LossRed
                    )
                }
                LinearProgressIndicator(
                    progress   = { ratio },
                    modifier   = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
                    color      = if (p.netProfitCents >= 0) KashColors.ProfitGreen else KashColors.LossRed,
                    trackColor = KashColors.Border,
                    strokeCap  = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun ProductRankCard(
    product: ProductProfitability,
    rank: Int,
    metricLabel: String,
    metricValue: String,
    metricColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(KashColors.Surface)
            .border(1.dp, KashColors.Border, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Text(
                text  = "#$rank",
                style = MaterialTheme.typography.labelMedium,
                color = if (rank == 1) KashColors.Accent else KashColors.OnSurfaceFaint
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text     = product.productName,
                style    = MaterialTheme.typography.bodyLarge,
                color    = KashColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text  = "margem ${product.marginPercent.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = KashColors.OnSurfaceMuted
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(metricValue, style = MaterialTheme.typography.titleMedium, color = metricColor)
            Text(metricLabel, style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceFaint)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text     = title.uppercase(),
        style    = MaterialTheme.typography.labelMedium,
        color    = KashColors.OnSurfaceMuted,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

private fun formatCents(cents: Long): String {
    val reais    = abs(cents) / 100
    val centavos = abs(cents) % 100
    val sign     = if (cents < 0) "-" else ""
    return "${sign}R\$ $reais,${"$centavos".padStart(2, '0')}"
}
