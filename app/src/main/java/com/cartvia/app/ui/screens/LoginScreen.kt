package com.cartvia.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.*
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.util.ApiResult

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    var showDevDialog by remember { mutableStateOf(false) }
    var devKey by remember { mutableStateOf("") }
    
    val loginState by viewModel.loginState.collectAsState()
    var statusState by remember { mutableStateOf<StatusState?>(null) }

    LaunchedEffect(loginState) {
        when (val result = loginState) {
            is ApiResult.Loading -> statusState = StatusState.Loading
            is ApiResult.Success -> {
                statusState = StatusState.Success(
                    message = "Login Successful!",
                    subMessage = "Welcome back to Cartvia."
                )
            }
            is ApiResult.Error -> {
                statusState = StatusState.Error(
                    message = result.message ?: "Invalid credentials. Please try again."
                )
            }
            is ApiResult.NetworkError -> {
                statusState = StatusState.Error(
                    message = "Network error. Please check your connection."
                )
            }
            null -> statusState = null
        }
    }

    statusState?.let { state ->
        StatusDialog(
            state = state,
            onDismiss = { 
                statusState = null
                viewModel.resetLoginState() 
            },
            onConfirm = if (state is StatusState.Success) {
                { onLoginSuccess() }
            } else null
        )
    }

    if (showDevDialog) {
        AlertDialog(
            onDismissRequest = { showDevDialog = false },
            title = { Text(text = "Are you developer?") },
            text = {
                Column {
                    Text(text = "Enter Dev Key to run on local instance bypass mode instead of API.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = devKey,
                        onValueChange = { devKey = it },
                        label = { Text("Enter Dev Key") },
                        placeholder = { Text("Dev Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDevDialog = false
                        viewModel.loginDevBypass(devKey)
                    }
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDevDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Sign in to access your digital wallet",
            style = MaterialTheme.typography.bodyMedium,
            color = NeutralGray
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        CartviaInputField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            placeholder = "Enter your email",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        CartviaInputField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Enter your password",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = NeutralGray
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onForgotPasswordClick)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        CartviaButton(
            text = "Sign In",
            onClick = { viewModel.login(email, password) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = buildAnnotatedString {
                append("Don't have an account? ")
                withStyle(SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)) {
                    append("Sign Up")
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = NeutralGray,
            modifier = Modifier.clickable(onClick = onRegisterClick)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { showDevDialog = true }) {
            Text(
                text = "Developer Bypass",
                color = PrimaryBlue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
