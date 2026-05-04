package com.kash.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (state.feedback != null) {
        LaunchedEffect(state.feedback) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearFeedback()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(KashColors.Background)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            Text(
                text  = "Histórico",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = KashColors.OnSurface
            )
        }

        // Filter bar
        FilterBar(
            current   = state.filter,
            onSelect  = viewModel::setFilter
        )

        if (state.feedback != null) {
            Text(
                text     = state.feedback!!,
                color    = KashColors.ProfitGreen,
                style    = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

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
                            TxRow(tx, onClick = { viewModel.selectTransaction(tx) })
                            if (index < state.visible.lastIndex)
                                HorizontalDivider(color = KashColors.Border, thickness = 0.5.dp)
                        }
                    }
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }

    // Detail bottom sheet
    state.selectedTx?.let { tx ->
        TransactionDetailSheet(
            tx          = tx,
            onEdit      = { showEditDialog = true },
            onDelete    = { showDeleteConfirm = true },
            onDismiss   = { viewModel.clearSelected() }
        )
    }

    // Edit dialog
    if (showEditDialog && state.selectedTx != null) {
        TransactionEditDialog(
            tx       = state.selectedTx!!,
            onSave   = { amount, desc, type, catId ->
                viewModel.updateTransaction(state.selectedTx!!, amount, desc, type, catId)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    // Delete confirmation
    if (showDeleteConfirm && state.selectedTx != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title            = { Text("Deletar transação?", color = KashColors.OnSurface) },
            text             = { Text("Esta ação não pode ser desfeita.") },
            confirmButton    = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(state.selectedTx!!.id)
                    showDeleteConfirm = false
                }) {
                    Text("Deletar", color = KashColors.LossRed)
                }
            },
            dismissButton    = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") } },
            containerColor   = KashColors.Surface
        )
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
private fun TxRow(tx: TransactionResponseDto, onClick: () -> Unit = {}) {
    val isInflow = tx.type == "INFLOW"
    val emoji    = categoryEmoji(tx.categoryName)
    val dateFmt  = remember { SimpleDateFormat("d MMM", Locale("pt", "BR")) }
    val dateStr  = remember(tx.createdAt) { dateFmt.format(Date(tx.createdAt)) }

    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick = onClick),
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoryBadge(tx.categoryName)
                    TypeBadge(isInflow)
                    Text(dateStr, style = MaterialTheme.typography.labelSmall, color = KashColors.OnSurfaceFaint, maxLines = 1)
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
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
            style    = MaterialTheme.typography.labelSmall,
            color    = KashColors.OnSurfaceMuted,
            maxLines = 1
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
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
            style    = MaterialTheme.typography.labelSmall,
            color    = color,
            maxLines = 1
        )
    }
}

@Composable
private fun TransactionDetailSheet(
    tx: TransactionResponseDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val isInflow = tx.type == "INFLOW"
    val dateFmt = remember { SimpleDateFormat("d MMM yyyy, HH:mm", Locale("pt", "BR")) }
    val dateStr = remember(tx.createdAt) { dateFmt.format(Date(tx.createdAt)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = KashColors.Surface,
        scrimColor       = KashColors.Background.copy(alpha = 0.32f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text  = "Detalhes",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = KashColors.OnSurface
            )
            Spacer(Modifier.height(20.dp))

            Text(
                text  = "${if (isInflow) "+ " else "- "}${formatCurrency(tx.amountCents)}",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = if (isInflow) KashColors.ProfitGreen else KashColors.LossRed
            )
            Spacer(Modifier.height(16.dp))

            DetailRow(label = "Tipo", value = if (isInflow) "Entrada" else "Saída")
            DetailRow(label = "Categoria", value = tx.categoryName)
            DetailRow(label = "Descrição", value = tx.description ?: "Sem descrição")
            DetailRow(label = "Data", value = dateStr)

            Spacer(Modifier.height(24.dp))

            Row(
                modifier         = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick  = onEdit,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(containerColor = KashColors.Accent)
                ) {
                    Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Editar")
                }
                Button(
                    onClick  = onDelete,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(containerColor = KashColors.LossRed)
                ) {
                    Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Deletar")
                }
            }

            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = KashColors.OnSurfaceMuted)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurface)
    }
}

@Composable
private fun TransactionEditDialog(
    tx: TransactionResponseDto,
    onSave: (Long, String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var amountStr by remember { mutableStateOf((tx.amountCents / 100).toString()) }
    var description by remember { mutableStateOf(tx.description ?: "") }
    var type by remember { mutableStateOf(tx.type) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("Editar transação", color = KashColors.OnSurface) },
        text             = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = amountStr,
                    onValueChange = { amountStr = it.filter(Char::isDigit).take(10) },
                    label         = { Text("Valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    colors        = kashFieldColors()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { type = "INFLOW" },
                        modifier = Modifier.weight(1f),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (type == "INFLOW") KashColors.ProfitGreen else KashColors.SurfaceVariant
                        )
                    ) { Text("Entrada") }
                    Button(
                        onClick = { type = "OUTFLOW" },
                        modifier = Modifier.weight(1f),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (type == "OUTFLOW") KashColors.LossRed else KashColors.SurfaceVariant
                        )
                    ) { Text("Saída") }
                }

                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = { Text("Descrição") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    colors        = kashFieldColors()
                )
            }
        },
        confirmButton    = {
            Button(
                onClick = {
                    val amount = amountStr.toLongOrNull()?.times(100) ?: tx.amountCents
                    onSave(amount, description.trim(), type, tx.categoryId)
                },
                colors  = ButtonDefaults.buttonColors(containerColor = KashColors.Accent)
            ) { Text("Salvar", color = KashColors.Background) }
        },
        dismissButton    = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        containerColor   = KashColors.Surface
    )
}

private fun kashFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = KashColors.Accent,
    unfocusedBorderColor = KashColors.Border,
    focusedTextColor     = KashColors.OnSurface,
    unfocusedTextColor   = KashColors.OnSurface,
    cursorColor          = KashColors.Accent
)
