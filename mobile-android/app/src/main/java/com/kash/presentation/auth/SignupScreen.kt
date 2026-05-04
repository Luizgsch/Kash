package com.kash.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kash.presentation.theme.KashColors

@Composable
fun SignupScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val state = viewModel.uiState

    LaunchedEffect(state) {
        if (state is LoginViewModel.UiState.Success) {
            onSignupSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KashColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text  = "Criar Conta",
                style = MaterialTheme.typography.displayMedium,
                color = KashColors.OnSurface
            )
            Text(
                text  = "Cadastre-se para começar",
                style = MaterialTheme.typography.bodyMedium,
                color = KashColors.OnSurfaceMuted
            )

            Spacer(Modifier.height(20.dp))

            KashTextField(
                value         = viewModel.name,
                onValueChange = viewModel::onName,
                label         = "Nome (opcional)",
                imeAction     = ImeAction.Next,
                onImeAction   = { focusManager.moveFocus(FocusDirection.Down) }
            )

            KashTextField(
                value         = viewModel.email,
                onValueChange = viewModel::onEmail,
                label         = "E-mail",
                keyboardType  = KeyboardType.Email,
                imeAction     = ImeAction.Next,
                onImeAction   = { focusManager.moveFocus(FocusDirection.Down) }
            )

            KashPasswordField(
                value         = viewModel.password,
                onValueChange = viewModel::onPassword,
                onImeAction   = {
                    focusManager.clearFocus()
                    viewModel.register()
                }
            )

            if (state is LoginViewModel.UiState.Error) {
                Text(
                    text  = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KashColors.LossRed
                )
            }

            Spacer(Modifier.height(6.dp))

            Button(
                onClick  = {
                    focusManager.clearFocus()
                    viewModel.register()
                },
                enabled  = state !is LoginViewModel.UiState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = KashColors.Accent,
                    disabledContainerColor = KashColors.Accent.copy(alpha = 0.4f)
                )
            ) {
                if (state is LoginViewModel.UiState.Loading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = KashColors.Background,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Criar Conta", color = KashColors.Background, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Já tem conta? Faça login", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
