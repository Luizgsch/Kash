package com.kash.presentation.spaces

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.remote.dto.CategoryManageDto
import com.kash.data.remote.dto.WalletDto
import com.kash.presentation.theme.KashColors

@Composable
fun SpacesScreen(viewModel: SpacesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreate by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<WalletDto?>(null) }
    var deleteTarget by remember { mutableStateOf<WalletDto?>(null) }
    var addCategoryWalletId by remember { mutableStateOf<String?>(null) }

    if (state.feedback != null) {
        LaunchedEffect(state.feedback) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearFeedback()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(KashColors.Background)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text  = "Espaços",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = KashColors.OnSurface
                )
                Text(
                    text  = "Gerencie seus espaços e categorias",
                    style = MaterialTheme.typography.bodySmall,
                    color = KashColors.OnSurfaceMuted
                )
            }
            IconButton(onClick = { showCreate = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Novo espaço", tint = KashColors.ProfitGreen)
            }
        }

        if (state.feedback != null) {
            Text(
                text     = state.feedback!!,
                color    = KashColors.LossRed,
                style    = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KashColors.Accent)
            }
            state.error != null -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.error!!, color = KashColors.LossRed)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { viewModel.load() }) { Text("Tentar novamente") }
            }
            state.wallets.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum espaço ainda. Crie o primeiro.", color = KashColors.OnSurfaceMuted)
            }
            else -> LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.wallets, key = { it.id }) { wallet ->
                    WalletCard(
                        wallet      = wallet,
                        expanded    = state.expandedWalletId == wallet.id,
                        categories  = state.categories[wallet.id] ?: emptyList(),
                        onToggle    = { viewModel.toggleExpand(wallet.id) },
                        onRename    = { editingWallet = wallet },
                        onDelete    = { deleteTarget = wallet },
                        onAddCat    = { addCategoryWalletId = wallet.id },
                        onDeleteCat = { catId -> viewModel.deleteCategory(wallet.id, catId) }
                    )
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }

    // Create wallet dialog
    if (showCreate) {
        NameDialog(
            title       = "Novo espaço",
            initial     = "",
            confirmText = "Criar",
            onConfirm   = { name -> viewModel.create(name); showCreate = false },
            onDismiss   = { showCreate = false }
        )
    }

    // Rename wallet dialog
    editingWallet?.let { w ->
        NameDialog(
            title       = "Renomear",
            initial     = w.name,
            confirmText = "Salvar",
            onConfirm   = { name -> viewModel.rename(w.id, name); editingWallet = null },
            onDismiss   = { editingWallet = null }
        )
    }

    // Add category dialog
    addCategoryWalletId?.let { wid ->
        NameDialog(
            title       = "Nova categoria",
            initial     = "",
            confirmText = "Criar",
            onConfirm   = { name -> viewModel.createCategory(wid, name); addCategoryWalletId = null },
            onDismiss   = { addCategoryWalletId = null }
        )
    }

    // Delete wallet confirm
    deleteTarget?.let { w ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title            = { Text("Excluir \"${w.name}\"?", color = KashColors.OnSurface) },
            text             = { Text("Apagará permanentemente todos os registros e categorias deste espaço.") },
            confirmButton    = {
                TextButton(onClick = { viewModel.delete(w.id); deleteTarget = null }) {
                    Text("Excluir", color = KashColors.LossRed)
                }
            },
            dismissButton    = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") }
            },
            containerColor   = KashColors.Surface
        )
    }
}

@Composable
private fun WalletCard(
    wallet: WalletDto,
    expanded: Boolean,
    categories: List<CategoryManageDto>,
    onToggle: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onAddCat: () -> Unit,
    onDeleteCat: (String) -> Unit
) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KashColors.Surface)
    ) {
        Column {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Folder, null, tint = KashColors.ProfitGreen, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        wallet.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = KashColors.OnSurface
                    )
                    Text(
                        text  = "${wallet.transactionCount} transação(ões) · ${wallet.categories.size} categoria(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = KashColors.OnSurfaceMuted
                    )
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        null, tint = KashColors.OnSurfaceMuted, modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onRename) {
                    Icon(Icons.Outlined.Edit, null, tint = KashColors.OnSurfaceMuted, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, null, tint = KashColors.LossRed, modifier = Modifier.size(18.dp))
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(start = 48.dp, end = 8.dp, bottom = 12.dp)) {
                    HorizontalDivider(color = KashColors.Border, thickness = 0.5.dp, modifier = Modifier.padding(end = 8.dp))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(end = 8.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Categorias", style = MaterialTheme.typography.labelMedium, color = KashColors.OnSurfaceMuted)
                        TextButton(
                            onClick      = onAddCat,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(14.dp), tint = KashColors.Accent)
                            Spacer(Modifier.width(4.dp))
                            Text("Adicionar", style = MaterialTheme.typography.labelSmall, color = KashColors.Accent)
                        }
                    }
                    if (categories.isEmpty()) {
                        Text(
                            "Nenhuma categoria",
                            style    = MaterialTheme.typography.bodySmall,
                            color    = KashColors.OnSurfaceFaint,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        categories.forEach { cat ->
                            Row(
                                modifier          = Modifier.fillMaxWidth().padding(vertical = 4.dp, end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Label, null, tint = KashColors.OnSurfaceFaint, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    cat.name,
                                    modifier = Modifier.weight(1f),
                                    style    = MaterialTheme.typography.bodySmall,
                                    color    = KashColors.OnSurface
                                )
                                IconButton(
                                    onClick  = { onDeleteCat(cat.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Outlined.Delete, null, tint = KashColors.LossRed, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NameDialog(title: String, initial: String, confirmText: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text(title, color = KashColors.OnSurface) },
        text             = {
            OutlinedTextField(
                value         = text,
                onValueChange = { text = it },
                label         = { Text("Nome") },
                singleLine    = true,
                shape         = RoundedCornerShape(10.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = KashColors.Accent,
                    unfocusedBorderColor = KashColors.Border,
                    focusedTextColor     = KashColors.OnSurface,
                    unfocusedTextColor   = KashColors.OnSurface,
                    cursorColor          = KashColors.Accent
                )
            )
        },
        confirmButton    = {
            Button(
                onClick = { if (text.isNotBlank()) onConfirm(text.trim()) },
                colors  = ButtonDefaults.buttonColors(containerColor = KashColors.Accent)
            ) { Text(confirmText, color = KashColors.Background) }
        },
        dismissButton    = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        containerColor   = KashColors.Surface
    )
}
