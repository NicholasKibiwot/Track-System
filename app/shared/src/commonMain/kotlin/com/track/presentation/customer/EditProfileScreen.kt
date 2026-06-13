package com.track.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    authViewModel: AuthViewModel,
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    // Pre-fill fields from the current user
    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var phone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
    var dob by remember(currentUser) { mutableStateOf(currentUser?.dob ?: "") }
    var country by remember(currentUser) { mutableStateOf(currentUser?.country ?: "") }
    var shippingAddress by remember(currentUser) { mutableStateOf(currentUser?.shippingAddress ?: "") }

    var localError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        containerColor = Color.White,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(100.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFF4C84FF),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Section
            Text(
                "Personal Information",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileEditField(label = "Full Name", value = name, onValueChange = { name = it }, icon = Icons.Default.Person)
            
            ProfileEditField(
                label = "Email Address",
                value = email,
                onValueChange = { },
                isReadOnly = true,
                icon = Icons.Default.Email,
                trailingContent = {
                    Text("VERIFIED", color = Color(0xFF4C84FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            )

            ProfileEditField(label = "Phone Number", value = phone, onValueChange = { phone = it }, icon = Icons.Default.Phone)
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Shipping & Location",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileEditField(label = "Shipping Address", value = shippingAddress, onValueChange = { shippingAddress = it }, icon = Icons.Default.LocationOn)
            ProfileEditField(label = "Country", value = country, onValueChange = { country = it }, icon = Icons.Default.Public)
            ProfileEditField(label = "Date of Birth", value = dob, onValueChange = { dob = it }, placeholder = "dd/mm/yyyy", icon = Icons.Default.Cake)

            // Error Message
            val displayError = localError ?: errorMessage
            displayError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(vertical = 12.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    localError = null
                    if (name.isBlank()) {
                        localError = "Name cannot be empty"
                        return@Button
                    }
                    authViewModel.updateProfile(
                        name = name,
                        phone = phone,
                        shippingAddress = shippingAddress,
                        dob = dob,
                        country = country,
                        onSuccess = onSaveSuccess
                    ) { localError = it }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1C1E)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String = "",
    isReadOnly: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
            trailingIcon = trailingContent,
            shape = RoundedCornerShape(12.dp),
            readOnly = isReadOnly,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4C84FF),
                unfocusedBorderColor = Color(0xFFE9ECEF),
                disabledBorderColor = Color(0xFFE9ECEF),
                focusedContainerColor = if (isReadOnly) Color(0xFFF8F9FA) else Color.White,
                unfocusedContainerColor = if (isReadOnly) Color(0xFFF8F9FA) else Color.White
            )
        )
    }
}

