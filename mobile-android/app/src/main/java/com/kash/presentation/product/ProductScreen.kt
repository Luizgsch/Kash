package com.kash.presentation.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.domain.model.Product
import com.kash.presentation.theme.KashColors
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(viewModel: ProductViewModel = hiltViewModel()) {
    val products   by viewModel.products.collectAsStateWithLifecycle()
    val sheetState  = viewModel.sheetState

    Scaffold(
        containerColor = KashColors.Background,
        topBar = { ProductTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = viewModel::openAdd,
                containerColor = KashColors.Accent,
                contentColor   = KashColors.Background,
                shape          = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Novo produto")
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            EmptyProductList(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(padding),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductRow(
                        product  = product,
                        onEdit   = { viewModel.openEdit(product) },
                        onDelete = { viewModel.delete(product) }
                    )
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }

    // Bottom sheet Add / Edit
    when (val s = sheetState) {
        is ProductViewModel.SheetState.Adding ->
            ProductSheet(
                title      = "Novo Produto",
                initial    = null,
                isSaving   = viewModel.isSaving,
                error      = viewModel.saveError,
                onDismiss  = viewModel::dismissSheet,
                onSave     = { name, sale, cost, stock ->
                    viewModel.save(name, sale, cost, stock)
                }
            )
        is ProductViewModel.SheetState.Editing ->
            ProductSheet(
                title      = "Editar Produto",
                initial    = s.product,
                isSaving   = viewModel.isSaving,
                error      = viewModel.saveError,
                onDismiss  = viewModel::dismissSheet,
                onSave     = { name, sale, cost, stock ->
                    viewModel.save(name, sale, cost, stock, existing = s.product)
                }
            )
        ProductViewModel.SheetState.Hidden -> Unit
    }
}

// ── Top Bar ────────────────────────────────────────────────────────────────────

@Composable
private fun ProductTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text("Produtos", style = MaterialTheme.typography.headlineLarge, color = KashColors.OnSurface)
    }
}

// ── Product Row ────────────────────────────────────────────────────────────────

@Composable
private fun ProductRow(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val margin = if (product.salePriceCents > 0)
        ((product.salePriceCents - product.costPriceCents).toFloat() / product.salePriceCents * 100).toInt()
    else 0
    val marginColor = when {
        margin >= 30 -> KashColors.ProfitGreen
        margin >= 10 -> KashColors.Accent
        else         -> KashColors.LossRed
    }
    var showDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KashColors.Surface)
            .border(1.dp, KashColors.Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text     = product.name,
                    style    = MaterialTheme.typography.bodyLarge,
                    color    = KashColors.OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = "estoque: ${product.currentStock}",
                    style = MaterialTheme.typography.labelMedium,
                    color = KashColors.OnSurfaceFaint
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Edit, null, tint = KashColors.OnSurfaceMuted, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { showDelete = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.DeleteOutline, null, tint = KashColors.LossRed, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = KashColors.Border)
        Spacer(Modifier.height(10.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PriceLabel("VENDA",  formatCents(product.salePriceCents), KashColors.OnSurface,    Modifier.weight(1f))
            PriceLabel("CUSTO",  formatCents(product.costPriceCents), KashColors.OnSurfaceMuted, Modifier.weight(1f))
            PriceLabel("MARGEM", "$margin%",                           marginColor,              Modifier.weight(1f))
        }
    }

    // Confirm delete dialog
    if (showDelete) {
        AlertDialog(
            onDismissRequest  = { showDelete = false },
            containerColor    = KashColors.Surface,
            title = { Text("Excluir produto?", color = KashColors.OnSurface) },
            text  = {
                Text(
                    "\"${product.name}\" será removido permanentemente.",
                    color = KashColors.OnSurfaceMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showDelete = false; onDelete() }) {
                    Text("Excluir", color = KashColors.LossRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) {
                    Text("Cancelar", color = KashColors.OnSurfaceMuted)
                }
            }
        )
    }
}

@Composable
private fun PriceLabel(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceFaint)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

// ── Add/Edit Sheet ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSheet(
    title: String,
    initial: Product?,
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, salePriceCents: Long, costPriceCents: Long, stock: Int) -> Unit
) {
    var name      by remember { mutableStateOf(initial?.name ?: "") }
    var salePrice by remember { mutableStateOf(if (initial != null) formatDecimal(initial.salePriceCents) else "") }
    var costPrice by remember { mutableStateOf(if (initial != null) formatDecimal(initial.costPriceCents) else "") }
    var stock     by remember { mutableStateOf(initial?.currentStock?.toString() ?: "0") }

    val margin = run {
        val s = parseCents(salePrice)
        val c = parseCents(costPrice)
        if (s > 0) ((s - c).toFloat() / s * 100).toInt() else 0
    }

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
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, color = KashColors.OnSurface)

            // Nome
            KashField(value = name, onValueChange = { name = it }, label = "Nome do Produto")

            // Preços lado a lado
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KashField(
                    value         = salePrice,
                    onValueChange = { salePrice = it },
                    label         = "Preço de Venda",
                    prefix        = "R$",
                    keyboard      = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
                KashField(
                    value         = costPrice,
                    onValueChange = { costPrice = it },
                    label         = "Preço de Custo",
                    prefix        = "R$",
                    keyboard      = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
            }

            // Estoque + margem calculada
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                KashField(
                    value         = stock,
                    onValueChange = { stock = it.filter { c -> c.isDigit() } },
                    label         = "Estoque",
                    keyboard      = KeyboardType.Number,
                    modifier      = Modifier.weight(1f)
                )
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KashColors.SurfaceVariant)
                        .border(1.dp, KashColors.Border, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 16.dp)
                ) {
                    Text("MARGEM", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceFaint)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$margin%",
                        style = MaterialTheme.typography.titleMedium,
                        color = when {
                            margin >= 30 -> KashColors.ProfitGreen
                            margin >= 10 -> KashColors.Accent
                            else         -> KashColors.LossRed
                        }
                    )
                }
            }

            // Erro
            if (error != null) {
                Text(error, style = MaterialTheme.typography.bodyMedium, color = KashColors.LossRed)
            }

            // Salvar
            Button(
                onClick  = {
                    onSave(name, parseCents(salePrice), parseCents(costPrice), stock.toIntOrNull() ?: 0)
                },
                enabled  = !isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = KashColors.Accent)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = KashColors.Background, strokeWidth = 2.dp)
                } else {
                    Text("Salvar", color = KashColors.Background, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun KashField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    prefix: String? = null,
    keyboard: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label) },
        prefix         = prefix?.let { { Text(it, color = KashColors.OnSurfaceMuted) } },
        modifier       = modifier.fillMaxWidth(),
        singleLine     = true,
        keyboardOptions= KeyboardOptions(keyboardType = keyboard),
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = KashColors.Accent,
            unfocusedBorderColor    = KashColors.Border,
            focusedLabelColor       = KashColors.Accent,
            unfocusedLabelColor     = KashColors.OnSurfaceMuted,
            focusedTextColor        = KashColors.OnSurface,
            unfocusedTextColor      = KashColors.OnSurface,
            cursorColor             = KashColors.Accent,
            focusedContainerColor   = KashColors.SurfaceVariant,
            unfocusedContainerColor = KashColors.SurfaceVariant
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

// ── Empty State ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyProductList(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.Inventory, null, tint = KashColors.OnSurfaceFaint, modifier = Modifier.size(48.dp))
            Text("Nenhum produto", style = MaterialTheme.typography.bodyLarge, color = KashColors.OnSurfaceMuted)
            Text("Toque em + para adicionar", style = MaterialTheme.typography.bodyMedium, color = KashColors.OnSurfaceFaint)
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────

private fun formatCents(cents: Long): String {
    val r = abs(cents) / 100
    val c = abs(cents) % 100
    return "R\$ $r,${"$c".padStart(2, '0')}"
}

/** "12.50" ou "12,50" → 1250 centavos */
private fun parseCents(input: String): Long {
    val normalized = input.replace(",", ".")
    return (normalized.toDoubleOrNull() ?: 0.0).let { (it * 100).toLong() }
}

/** 1250 → "12.50" */
private fun formatDecimal(cents: Long): String =
    "%.2f".format(cents / 100.0).replace(",", ".")
