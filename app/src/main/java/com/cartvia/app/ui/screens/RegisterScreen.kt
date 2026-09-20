package com.cartvia.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.cartvia.app.data.model.RegisterRequest
import com.cartvia.app.ui.components.*
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.util.ApiResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    val registerState by viewModel.registerState.collectAsState()
    var statusState by remember { mutableStateOf<StatusState?>(null) }

    LaunchedEffect(registerState) {
        when (val result = registerState) {
            is ApiResult.Loading -> statusState = StatusState.Loading
            is ApiResult.Success -> {
                statusState = StatusState.Success(
                    message = "Well Done!",
                    subMessage = "Your account has been successfully created."
                )
            }
            is ApiResult.Error -> {
                statusState = StatusState.Error(
                    message = result.message ?: "Registration failed. Please try again."
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
                viewModel.resetRegisterState() 
            },
            onConfirm = if (state is StatusState.Success) {
                { onRegisterSuccess() }
            } else null
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = onLoginClick,
            modifier = Modifier.offset(x = (-12).dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Create an account to continue!",
            style = MaterialTheme.typography.bodyMedium,
            color = NeutralGray
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        CartviaInputField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name",
            placeholder = "Enter your first name",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CartviaInputField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Last Name",
            placeholder = "Enter your last name",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CartviaInputField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            placeholder = "Enter your email",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CartviaInputField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            placeholder = "Enter your phone number",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CartviaInputField(
            value = password,
            onValueChange = { password = it },
            label = "Choose Password",
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
        
        Spacer(modifier = Modifier.height(40.dp))
        
        CartviaButton(
            text = "Create Account",
            onClick = { 
                viewModel.register(
                    RegisterRequest(
                        username = email,
                        name = "$firstName $lastName",
                        email = email,
                        phone = phone,
                        password = password
                    )
                )
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)) {
                        append("Log in")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = NeutralGray,
                modifier = Modifier.clickable(onClick = onLoginClick)
            )
        }
    }
}
