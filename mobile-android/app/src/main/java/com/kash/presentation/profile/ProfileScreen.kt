package com.kash.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kash.presentation.theme.KashColors

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showEditName by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    if (state.feedback != null) {
        LaunchedEffect(state.feedback) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearFeedback()
        }
    }

    Column(
        modifier            = Modifier.fillMaxSize().background(KashColors.Background).padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text  = "Perfil",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = KashColors.OnSurface
        )

        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KashColors.Accent)
            }
            state.error != null -> Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.error!!, color = KashColors.LossRed)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { viewModel.load() }) { Text("Tentar novamente") }
            }
            state.profile != null -> {
                val profile = state.profile!!
                val initials = (profile.name ?: profile.email ?: "?")
                    .split(" ", "@").filter { it.isNotBlank() }.take(2)
                    .joinToString("") { it.first().uppercaseChar().toString() }

                // Avatar + name card
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KashColors.Surface)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier         = Modifier.size(56.dp).clip(CircleShape).background(KashColors.SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = initials,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = KashColors.Accent
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text  = profile.name ?: "Sem nome",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = KashColors.OnSurface
                            )
                            Text(
                                text  = profile.email ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = KashColors.OnSurfaceMuted
                            )
                            if (profile.role.isNotBlank() && profile.role != "member") {
                                Spacer(Modifier.height(4.dp))
                                Surface(shape = RoundedCornerShape(50), color = KashColors.SurfaceVariant) {
                                    Text(
                                        text     = profile.role,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style    = MaterialTheme.typography.labelSmall,
                                        color    = KashColors.OnSurfaceMuted
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { showEditName = true }) {
                            Icon(Icons.Outlined.Edit, null, tint = KashColors.OnSurfaceMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (state.feedback != null) {
                    Text(state.feedback!!, style = MaterialTheme.typography.bodySmall, color = KashColors.OnSurfaceMuted)
                }

                Spacer(Modifier.weight(1f))

                // Logout button
                OutlinedButton(
                    onClick  = { showLogoutConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = KashColors.LossRed),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, KashColors.LossRed.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Outlined.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sair da conta")
                }

                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }

    // Edit name dialog
    if (showEditName && state.profile != null) {
        var draft by remember { mutableStateOf(state.profile?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showEditName = false },
            title            = { Text("Editar nome", color = KashColors.OnSurface) },
            text             = {
                OutlinedTextField(
                    value         = draft,
                    onValueChange = { draft = it },
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
                    onClick = { viewModel.updateName(draft); showEditName = false },
                    enabled = draft.isNotBlank(),
                    colors  = ButtonDefaults.buttonColors(containerColor = KashColors.Accent)
                ) {
                    if (state.saving) CircularProgressIndicator(Modifier.size(16.dp), color = KashColors.Background, strokeWidth = 2.dp)
                    else Text("Salvar", color = KashColors.Background)
                }
            },
            dismissButton    = { TextButton(onClick = { showEditName = false }) { Text("Cancelar") } },
            containerColor   = KashColors.Surface
        )
    }

    // Logout confirm
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title            = { Text("Sair da conta?", color = KashColors.OnSurface) },
            confirmButton    = {
                Button(
                    onClick = { showLogoutConfirm = false; onLogout() },
                    colors  = ButtonDefaults.buttonColors(containerColor = KashColors.LossRed)
                ) { Text("Sair", color = KashColors.OnSurface) }
            },
            dismissButton    = { TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancelar") } },
            containerColor   = KashColors.Surface
        )
    }
}
