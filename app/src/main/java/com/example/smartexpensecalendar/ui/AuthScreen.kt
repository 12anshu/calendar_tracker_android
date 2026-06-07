package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.R
import com.example.smartexpensecalendar.presentation.auth.AuthViewModel
import com.example.smartexpensecalendar.ui.components.AppLogoText
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.example.smartexpensecalendar.ui.navigation.Screen
import com.example.smartexpensecalendar.core.designsystem.theme.*

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    forceShow: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLoginForm by remember { mutableStateOf(false) }
    var showSkipWarning by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!forceShow) {
            viewModel.checkInitialChoice()
        }
    }

    LaunchedEffect(uiState.isChoiceMade) {
        if (uiState.isChoiceMade) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // New Branded Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(32.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppLogoText(
                textStyle = MaterialTheme.typography.headlineLarge,
                showTagline = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (!showLoginForm) {
                // Branded Google Button
                GoogleSignInButton(
                    onClick = { viewModel.continueWithGoogle(context) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthButton(
                    text = "Login with Email",
                    icon = Icons.Default.Email,
                    containerColor = SurfaceGlass,
                    contentColor = TextPrimary,
                    onClick = { showLoginForm = true }
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Text(
                    text = "Skip for now (Local Only)",
                    color = CyanGlow,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showSkipWarning = true }
                        .padding(8.dp)
                )
            } else {
                EmailLoginForm(
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    onBack = { showLoginForm = false },
                    onLogin = { email, password ->
                        viewModel.login(email, password)
                    }
                )
            }
        }

        if (showSkipWarning) {
            SkipWarningDialog(
                onDismiss = { showSkipWarning = false },
                onConfirm = { 
                    viewModel.skipAuth() 
                    showSkipWarning = false
                }
            )
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Continue with Google",
                color = Color(0xFF1F1F1F),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AuthButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun EmailLoginForm(
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Welcome", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Login to your account", color = TextSecondary, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanGlow),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanGlow),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = Color.Red, style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", fontWeight = FontWeight.Bold)
            }
        }
        
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back to options", color = TextSecondary)
        }
    }
}

@Composable
fun SkipWarningDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ColorTransport) },
        title = { Text("Risk of Data Loss", textAlign = TextAlign.Center) },
        text = {
            Text(
                "Without an account, your transaction history and rules are stored only on this device. If you uninstall the app, your data will be lost.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Understand & Skip", color = ColorTransport, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextPrimary)
            }
        }
    )
}
