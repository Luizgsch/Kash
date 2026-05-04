package com.kash.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.remote.dto.CategoryDto
import com.kash.presentation.dashboard.formatCurrency
import com.kash.presentation.theme.KashColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onDone: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KashColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text  = "Adicionar",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = KashColors.OnSurface
        )

        // Type toggle
        TypeToggle(type = state.type, onType = viewModel::setType)

        // Amount
        AmountField(digits = state.digits, onDigits = viewModel::setDigits)

        // Wallet selector (only when multiple wallets)
        if (state.wallets.size > 1) {
            WalletSelector(
                wallets    = state.wallets,
                selectedId = state.selectedWalletId,
                onSelect   = viewModel::setWallet
            )
        }

        // Categories
        val categories = state.selectedWallet?.categories ?: emptyList()
        if (categories.isNotEmpty()) {
            CategoryGrid(
                categories  = categories,
                selectedId  = state.selectedCategoryId,
                onSelect    = viewModel::setCategory
            )
        }

        // Description
        OutlinedTextField(
            value         = state.description,
            onValueChange = viewModel::setDescription,
            label         = { Text("Descrição (opcional)") },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(10.dp),
            singleLine    = true,
            colors        = kashFieldColors()
        )

        // Error
        if (state.error != null) {
            Text(state.error!!, color = KashColors.LossRed, style = MaterialTheme.typography.bodySmall)
        }

        // Save button
        Button(
            onClick  = { viewModel.save(onDone) },
            enabled  = state.amountCents > 0 && !state.saving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = KashColors.Accent,
                disabledContainerColor = KashColors.Accent.copy(alpha = 0.4f)
            )
        ) {
            if (state.saving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = KashColors.Background, strokeWidth = 2.dp)
            } else {
                Text("Salvar", color = KashColors.Background, style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun TypeToggle(type: String, onType: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KashColors.SurfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        listOf("INFLOW" to "Entrada", "OUTFLOW" to "Saída").forEach { (value, label) ->
            val selected = type == value
            val color    = when { selected && value == "INFLOW" -> KashColors.ProfitGreen; selected -> KashColors.LossRed; else -> KashColors.OnSurfaceFaint }
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) KashColors.Surface else KashColors.SurfaceVariant)
                    .clickable { onType(value) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = color)
            }
        }
    }
}

@Composable
private fun AmountField(digits: String, onDigits: (String) -> Unit) {
    val formatted = remember(digits) {
        val cents = digits.toLongOrNull() ?: 0L
        if (cents == 0L) "R$ 0,00"
        else NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(cents / 100.0)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text      = formatted,
            style     = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color     = KashColors.OnSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value         = digits,
            onValueChange = onDigits,
            label         = { Text("Valor em centavos") },
            placeholder   = { Text("Ex: 1500 = R$ 15,00") },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(10.dp),
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors        = kashFieldColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSelector(wallets: List<com.kash.data.remote.dto.WalletDto>, selectedId: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = wallets.find { it.id == selectedId }

    Column {
        Text("Espaço", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
        Spacer(Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value         = selected?.name ?: "",
                onValueChange = {},
                readOnly      = true,
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                shape         = RoundedCornerShape(10.dp),
                colors        = kashFieldColors()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                wallets.forEach { w ->
                    DropdownMenuItem(
                        text    = { Text(w.name) },
                        onClick = { onSelect(w.id); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGrid(categories: List<CategoryDto>, selectedId: String?, onSelect: (String?) -> Unit) {
    Column {
        Text("Categoria", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
        Spacer(Modifier.height(8.dp))
        val cols = 3
        categories.chunked(cols).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    val selected = cat.id == selectedId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) KashColors.SurfaceVariant else KashColors.Surface)
                            .border(1.dp, if (selected) KashColors.Accent else KashColors.Border, RoundedCornerShape(12.dp))
                            .clickable { onSelect(if (selected) null else cat.id) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text      = cat.name,
                            style     = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                            color     = if (selected) KashColors.Accent else KashColors.OnSurface,
                            textAlign = TextAlign.Center,
                            maxLines  = 2
                        )
                    }
                }
                repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun kashFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = KashColors.Accent,
    unfocusedBorderColor = KashColors.Border,
    focusedLabelColor    = KashColors.Accent,
    unfocusedLabelColor  = KashColors.OnSurfaceMuted,
    focusedTextColor     = KashColors.OnSurface,
    unfocusedTextColor   = KashColors.OnSurface,
    cursorColor          = KashColors.Accent
)
