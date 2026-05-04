package com.kash.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kash.R
import com.kash.presentation.theme.KashColors

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToSignup: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
    val activity     = context as? ComponentActivity ?: return

    var googleError by remember { mutableStateOf<String?>(null) }
    val state = viewModel.uiState

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            Log.d("GoogleLogin", "onActivityResult: resultCode=${result.resultCode}")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                Log.d("GoogleLogin", "ID Token obtido com sucesso")
                viewModel.loginWithGoogle(idToken)
            } else {
                Log.e("GoogleLogin", "ID Token é nulo")
                googleError = "Falha ao obter ID Token"
            }
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "ApiException: ${e.statusCode} - ${e.message}", e)
            googleError = "Google login falhou: ${e.message}"
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Exception: ${e.javaClass.simpleName} - ${e.message}", e)
            googleError = "Erro: ${e.message}"
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
            // Logo
            Text(
                text  = "Kash",
                style = MaterialTheme.typography.displayLarge,
                color = KashColors.OnSurface
            )
            Text(
                text  = "Gestão financeira inteligente",
                style = MaterialTheme.typography.bodyMedium,
                color = KashColors.OnSurfaceMuted
            )

            Spacer(Modifier.height(20.dp))

            // E-mail
            KashTextField(
                value         = viewModel.email,
                onValueChange = viewModel::onEmail,
                label         = "E-mail",
                keyboardType  = KeyboardType.Email,
                imeAction     = ImeAction.Next,
                onImeAction   = { focusManager.moveFocus(FocusDirection.Down) }
            )

            // Senha
            KashPasswordField(
                value         = viewModel.password,
                onValueChange = viewModel::onPassword,
                onImeAction   = {
                    focusManager.clearFocus()
                    viewModel.submit()
                }
            )

            // Erro e-mail/senha
            if (state is LoginViewModel.UiState.Error) {
                Text(
                    text  = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KashColors.LossRed
                )
            }

            Spacer(Modifier.height(6.dp))

            // Botão entrar
            Button(
                onClick  = {
                    focusManager.clearFocus()
                    googleError = null
                    viewModel.submit()
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
                    Text("Entrar", color = KashColors.Background, style = MaterialTheme.typography.titleMedium)
                }
            }

            // Divisor
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(Modifier.weight(1f), color = KashColors.Border)
                Text(
                    "  OU  ",
                    style = MaterialTheme.typography.labelMedium,
                    color = KashColors.OnSurfaceFaint
                )
                HorizontalDivider(Modifier.weight(1f), color = KashColors.Border)
            }

            // Botão Google
            OutlinedButton(
                onClick = {
                    Log.d("GoogleLogin", "Botão clicado")
                    googleError = null
                    try {
                        Log.d("GoogleLogin", "Criando GoogleSignInOptions")
                        val webClientId = context.getString(R.string.google_web_client_id)
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(webClientId)
                            .requestEmail()
                            .build()

                        Log.d("GoogleLogin", "Criando GoogleSignInClient")
                        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

                        Log.d("GoogleLogin", "Lançando signIn")
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    } catch (e: Exception) {
                        Log.e("GoogleLogin", "Erro ao lançar signIn: ${e.message}", e)
                        googleError = "Erro: ${e.message}"
                    }
                },
                enabled  = state !is LoginViewModel.UiState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                border   = BorderStroke(1.dp, KashColors.Border)
            ) {
                Text("Continuar com Google", style = MaterialTheme.typography.titleMedium, color = KashColors.OnSurface)
            }

            if (googleError != null) {
                Text(
                    text  = googleError!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KashColors.LossRed
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onNavigateToSignup) {
                Text("Não tem conta? Criar conta", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
internal fun KashTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label) },
        modifier       = Modifier.fillMaxWidth(),
        singleLine     = true,
        keyboardOptions= KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions= KeyboardActions(onAny = { onImeAction() }),
        colors         = kashTextFieldColors(),
        shape          = RoundedCornerShape(10.dp)
    )
}

@Composable
internal fun KashPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value                = value,
        onValueChange        = onValueChange,
        label                = { Text("Senha") },
        modifier             = Modifier.fillMaxWidth(),
        singleLine           = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions      = KeyboardActions(onDone = { onImeAction() }),
        trailingIcon         = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector        = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = null,
                    tint               = KashColors.OnSurfaceMuted
                )
            }
        },
        colors = kashTextFieldColors(),
        shape  = RoundedCornerShape(10.dp)
    )
}

@Composable
internal fun kashTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = KashColors.Accent,
    unfocusedBorderColor    = KashColors.Border,
    focusedLabelColor       = KashColors.Accent,
    unfocusedLabelColor     = KashColors.OnSurfaceMuted,
    focusedTextColor        = KashColors.OnSurface,
    unfocusedTextColor      = KashColors.OnSurface,
    cursorColor             = KashColors.Accent,
    focusedContainerColor   = KashColors.Surface,
    unfocusedContainerColor = KashColors.Surface
)
