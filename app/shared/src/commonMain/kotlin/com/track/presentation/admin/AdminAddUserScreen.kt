package com.track.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.track.models.User
import com.track.models.UserRole
import com.track.util.kmpViewModel
import com.track.presentation.admin.SuperAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddUserScreen(
    viewModel: SuperAdminViewModel = kmpViewModel<SuperAdminViewModel>(),
    onBackClick: () -> Unit = {},
    onUserAdded: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.STAFF) }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Internal Account") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AdminAddUserForm(
                name = name,
                onNameChange = { name = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                role = role,
                onRoleChange = { role = it },
                isLoading = isLoading
            )

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))
            
            AdminAddUserNote()

            Button(
                onClick = {
                    val newUser = User(
                        id = email, 
                        email = email,
                        name = name,
                        displayName = name,
                        role = role,
                        isActive = true
                    )
                    viewModel.createUser(newUser) {
                        onUserAdded()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text("Create Account")
            }
        }
    }
}

@Composable
private fun AdminAddUserForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    role: UserRole,
    onRoleChange: (UserRole) -> Unit,
    isLoading: Boolean
) {
    Text(
        "Account Details",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Full Name") },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
    )

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Work Email") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        enabled = !isLoading
    )

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Temporary Password") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
            }
        },
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        "Assign Role",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = role == UserRole.STAFF, onClick = { onRoleChange(UserRole.STAFF) })
        Text("Staff", modifier = Modifier.padding(start = 8.dp))
        Spacer(Modifier.width(16.dp))
        RadioButton(selected = role == UserRole.DRIVER, onClick = { onRoleChange(UserRole.DRIVER) })
        Text("Driver", modifier = Modifier.padding(start = 8.dp))
        Spacer(Modifier.width(16.dp))
        RadioButton(selected = role == UserRole.ADMIN, onClick = { onRoleChange(UserRole.ADMIN) })
        Text("Admin", modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun AdminAddUserNote() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Note: For security, the user record will be created in Firestore. You must ensure an matching Auth account exists in Firebase Console.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
