package com.track.presentation.customer

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.track.presentation.auth.GoogleAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLoginScreen(
    webClientId: String,
    onLoggedIn: (idToken: String) -> Unit,
    onEmailSignIn: (String, String) -> Unit = { _, _ -> },
    onEmailSignUp: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    var isSignInMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val googleAuthManager = remember {
        GoogleAuthManager(activity, webClientId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD180)) // Peachy orange background
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginModeToggle(
                    isSignInMode = isSignInMode,
                    onModeChange = { isSignInMode = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                EmailField(email = email, onEmailChange = { email = it })

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    password = password,
                    onPasswordChange = { password = it },
                    passwordVisible = passwordVisible,
                    onToggleVisibility = { passwordVisible = !passwordVisible }
                )

                if (!isSignInMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ConfirmPasswordField(
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it },
                        confirmPasswordVisible = confirmPasswordVisible,
                        onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
                    )
                } else {
                    RememberMeSection(
                        rememberMe = rememberMe,
                        onRememberMeChange = { rememberMe = it },
                        onForgotPasswordClick = onForgotPasswordClick
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SubmitButton(
                    isSignInMode = isSignInMode,
                    isLoading = isLoading,
                    onClick = {
                        if (isSignInMode) {
                            onEmailSignIn(email, password)
                        } else {
                            onEmailSignUp(email, password)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("-OR-", color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))

                SocialLoginIcons(
                    scope = scope,
                    googleAuthManager = googleAuthManager,
                    onLoggedIn = onLoggedIn,
                    onError = { error = it }
                )

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color(0xFFFF5252))
                }

                error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun LoginModeToggle(isSignInMode: Boolean, onModeChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFF5F5F5))
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModeToggleButton(
            text = "Sign In",
            isSelected = isSignInMode,
            onClick = { onModeChange(true) }
        )
        ModeToggleButton(
            text = "Sign Up",
            isSelected = !isSignInMode,
            onClick = { onModeChange(false) }
        )
    }
}

@Composable
fun RowScope.ModeToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFFFF5252) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (isSelected) Color.White else Color(0xFFFF5252),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun EmailField(email: String, onEmailChange: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        placeholder = { Text("Enter email or username", color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.LightGray
        ),
        singleLine = true
    )
}

@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text("Password", color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.LightGray
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Composable
fun ConfirmPasswordField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    TextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        placeholder = { Text("Confirm Password", color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.LightGray
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Composable
fun RememberMeSection(
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = onRememberMeChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF5252))
            )
            Text("Remember me", fontSize = 12.sp, color = Color.Gray)
        }
        Text(
            "Forgot Password?",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.clickable { onForgotPasswordClick() }
        )
    }
}

@Composable
fun SubmitButton(isSignInMode: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
        shape = RoundedCornerShape(25.dp)
    ) {
        Text(
            if (isSignInMode) "Sign In" else "Sign Up",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun SocialLoginIcons(
    scope: CoroutineScope,
    googleAuthManager: GoogleAuthManager,
    onLoggedIn: (String) -> Unit,
    onError: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialIcon(text = "f", color = Color(0xFF1877F2), onClick = { /* FB */ })
        SocialIcon(text = "t", color = Color(0xFF1DA1F2), onClick = { /* Twitter */ })
        SocialIcon(text = "G", color = Color(0xFFEA4335), onClick = {
            scope.launch {
                try {
                    val idToken = googleAuthManager.signInWithGoogleAndGetFirebaseIdToken()
                    onLoggedIn(idToken)
                } catch (e: Exception) {
                    onError(e.message ?: "Google sign in failed")
                }
            }
        })
    }
}

@Composable
fun SocialIcon(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}
