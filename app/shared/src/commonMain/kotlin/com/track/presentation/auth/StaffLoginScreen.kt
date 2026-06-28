package com.track.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.track.util.kmpViewModel
import com.track.models.UserRole

@Composable
fun StaffLoginScreen(
    onLoginSuccess: (role: String) -> Unit = {},
    viewModel: AuthViewModel = kmpViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1C1E)) 
    ) {
        StaffLoginDecorations()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            StaffLoginHeader()

            Spacer(modifier = Modifier.weight(1f))

            StaffLoginCard(
                email = email,
                onEmailChange = { email = it; viewModel.clearError() },
                password = password,
                onPasswordChange = { password = it; viewModel.clearError() },
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLoginClick = {
                    viewModel.login(email.trim(), password, expectedRole = UserRole.STAFF) { user ->
                        onLoginSuccess(user.role.name)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StaffLoginFooter()
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StaffLoginDecorations() {
    Box(
        modifier = Modifier
            .size(300.dp)
            .offset(x = 100.dp, y = (-100).dp)
            .clip(CircleShape)
            .background(Color(0xFF4C84FF).copy(alpha = 0.1f))
    )
}

@Composable
private fun StaffLoginHeader() {
    Surface(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.1f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Badge,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    
    Text(
        text = "Track Internal Portal",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White
    )
    Text(
        text = "Staff & Driver Management System",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.6f)
    )
}

@Composable
private fun StaffLoginFooter() {
    TextButton(onClick = { /* Contact Support */ }) {
        Text(
            "Locked out? Contact IT Support",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun StaffLoginCard(
    email: String,
    onEmailChange: (String) -> Unit,
    password: (String),
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))

            StaffLoginFields(
                email = email,
                onEmailChange = onEmailChange,
                password = password,
                onPasswordChange = onPasswordChange,
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                isLoading = isLoading
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1C1E)),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Unlock Portal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StaffLoginFields(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Work Email") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun StaffLoginScreenPreview() {
    MaterialTheme {
        StaffLoginScreen()
    }
}
