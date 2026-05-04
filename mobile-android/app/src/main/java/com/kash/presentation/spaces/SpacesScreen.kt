package com.kash.presentation.spaces

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.data.remote.dto.WalletDto
import com.kash.presentation.theme.KashColors

@Composable
fun SpacesScreen(viewModel: SpacesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreate by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<WalletDto?>(null) }
    var deleteTarget by remember { mutableStateOf<WalletDto?>(null) }

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
                    text  = "Classes (espaços)",
                    style = MaterialTheme.typography.bodySmall,
                    color = KashColors.OnSurfaceMuted
                )
            }
            IconButton(onClick = { showCreate = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Nova classe", tint = KashColors.ProfitGreen)
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
                Text("Nenhuma classe ainda. Crie a primeira.", color = KashColors.OnSurfaceMuted)
            }
            else -> LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.wallets, key = { it.id }) { wallet ->
                    WalletCard(
                        wallet    = wallet,
                        onRename  = { editingWallet = wallet },
                        onDelete  = { deleteTarget = wallet }
                    )
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }

    // Create dialog
    if (showCreate) {
        NameDialog(
            title       = "Nova classe",
            initial     = "",
            confirmText = "Criar",
            onConfirm   = { name -> viewModel.create(name); showCreate = false },
            onDismiss   = { showCreate = false }
        )
    }

    // Rename dialog
    editingWallet?.let { w ->
        NameDialog(
            title       = "Renomear",
            initial     = w.name,
            confirmText = "Salvar",
            onConfirm   = { name -> viewModel.rename(w.id, name); editingWallet = null },
            onDismiss   = { editingWallet = null }
        )
    }

    // Delete confirm
    deleteTarget?.let { w ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title            = { Text("Excluir \"${w.name}\"?") },
            text             = { Text("Apagará permanentemente todos os registros e categorias desta classe.") },
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
private fun WalletCard(wallet: WalletDto, onRename: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KashColors.Surface)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Folder, null, tint = KashColors.ProfitGreen, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(wallet.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = KashColors.OnSurface)
                if (wallet.transactionCount > 0) {
                    Text(
                        text  = "${wallet.transactionCount} transação(ões)",
                        style = MaterialTheme.typography.bodySmall,
                        color = KashColors.OnSurfaceMuted
                    )
                }
            }
            IconButton(onClick = onRename) {
                Icon(Icons.Outlined.Edit, null, tint = KashColors.OnSurfaceMuted, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, null, tint = KashColors.LossRed, modifier = Modifier.size(18.dp))
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
