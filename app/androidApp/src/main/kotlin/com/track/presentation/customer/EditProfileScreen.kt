package com.track.presentation.customer

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.presentation.viewmodel.AppAuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    authViewModel: AppAuthViewModel,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Completion Progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LinearProgressIndicator(
                        progress = { 0.8f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = Color(0xFF4C84FF),
                        trackColor = Color(0xFFE9ECEF)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("You only need 20% more!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "Complete your data, and get our voucher of free shipping fee!",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(100.dp).clip(CircleShape),
                    color = Color.LightGray
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        tint = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                        .shadow(1.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF4C84FF))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            ProfileEditField(label = "Name", value = name, onValueChange = { name = it })
            
            ProfileEditField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                isReadOnly = true,
                trailingContent = {
                    Surface(
                        color = Color(0xFFEBF2FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4C84FF), modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VERIFIED", color = Color(0xFF4C84FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )

            ProfileEditField(label = "Phone Number", value = phone, onValueChange = { phone = it })
            ProfileEditField(label = "Date of Birth", value = dob, onValueChange = { dob = it }, placeholder = "dd/mm/yyyy")
            ProfileEditField(label = "Country", value = country, onValueChange = { country = it }, placeholder = "Jakarta, Indonesia")

            // Error Message
            val displayError = localError ?: errorMessage
            displayError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        onSuccess = onSaveSuccess,
                        onError = { localError = it }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C84FF)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Saved Change", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isReadOnly: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingContent()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            shape = RoundedCornerShape(12.dp),
            readOnly = isReadOnly,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4C84FF),
                unfocusedBorderColor = Color(0xFFE9ECEF),
                disabledBorderColor = Color(0xFFE9ECEF)
            )
        )
    }
}
