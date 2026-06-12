package com.track.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.track.domain.models.UserRole
import com.track.presentation.viewmodel.AppAuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AppAuthViewModel,
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            LoginTopBar(onBackClick = onBackClick)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginHeader()

            Spacer(Modifier.height(40.dp))

            LoginForm(
                email = email,
                onEmailChange = {
                    email = it
                    viewModel.clearError()
                },
                password = password,
                onPasswordChange = {
                    password = it
                    viewModel.clearError()
                },
                isLoading = isLoading,
                errorMessage = errorMessage
            )

            Spacer(Modifier.height(16.dp))

            LoginButton(
                isLoading = isLoading,
                isEnabled = email.isNotBlank() && password.isNotBlank(),
                onClick = {
                    viewModel.login(email.trim(), password, expectedRole = UserRole.CUSTOMER) { user ->
                        onLoginSuccess(user.role.name)
                    }
                }
            )

            Spacer(Modifier.height(8.dp))
            
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }

            Spacer(Modifier.height(16.dp))
            
            // Note for customers: Staff login is in a separate app
            Text(
                text = "Staff or Driver? Use the Track Staff app.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Customer Sign In") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
private fun LoginHeader() {
    Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = "Track",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Welcome back, Customer",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email address") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        enabled = !isLoading
    )
    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading,
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible)
                        Icons.Default.VisibilityOff
                    else
                        Icons.Default.Visibility,
                    contentDescription = if (passwordVisible)
                        "Hide password"
                    else
                        "Show password"
                )
            }
        }
    )
    
    if (errorMessage != null) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = !isLoading && isEnabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
