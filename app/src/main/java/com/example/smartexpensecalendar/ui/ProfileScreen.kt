package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.presentation.profile.ProfileViewModel
import com.example.smartexpensecalendar.features.beta_audit.BetaAuditViewModel
import com.example.smartexpensecalendar.ui.components.PremiumFeatureCard
import com.example.smartexpensecalendar.ui.components.FintechBottomNav
import com.example.smartexpensecalendar.ui.navigation.Screen
import com.example.smartexpensecalendar.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    auditViewModel: BetaAuditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val auditStatus by auditViewModel.exportStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCurrencyPicker by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(auditStatus) {
        auditStatus?.let {
            snackbarHostState.showSnackbar(it)
            auditViewModel.clearStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundStart)
            )
        },
        bottomBar = {
            FintechBottomNav(navController = navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundStart
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Identity Header
            item {
                IdentityHeader(
                    name = uiState.profile.name,
                    email = uiState.profile.email,
                    authType = uiState.profile.authType,
                    onLoginClick = { navController.navigate(Screen.Auth.createRoute(force = true)) }
                )
            }

            // 1.5 Premium Promo
            item {
                PremiumFeatureCard(
                    onClick = { navController.navigate(Screen.Subscription.route) }
                )
            }

            // 2. Preferences Section
            item { ProfileSectionHeader("App Preferences") }
            item {
                PreferenceItem(
                    title = "Primary Currency",
                    subtitle = "Current: ${uiState.currencySymbol}",
                    icon = Icons.Default.Payments,
                    onClick = { showCurrencyPicker = true }
                )
            }
            item {
                SyncPreferenceItem(
                    enabled = uiState.autoSyncEnabled,
                    onToggle = { viewModel.toggleAutoSync(it) }
                )
            }

            // 3. Subscription Section
            item { ProfileSectionHeader("Plan & Billing") }
            item {
                PreferenceItem(
                    title = "Manage Subscription",
                    subtitle = "Current Plan: FREE • Tap to upgrade",
                    icon = Icons.Default.WorkspacePremium,
                    onClick = { navController.navigate(Screen.Subscription.route) }
                )
            }

            // 4. Cloud & Data
            item { ProfileSectionHeader("Cloud & Data") }
            item {
                PreferenceItem(
                    title = "Cloud Backup",
                    subtitle = if (uiState.profile.authType == "GOOGLE") "Syncing with Google" else "Link account to enable",
                    icon = Icons.Default.CloudUpload,
                    onClick = { if (uiState.profile.authType == "LOCAL") navController.navigate(Screen.Auth.createRoute(force = true)) }
                )
            }

            // 4. Support
            item { ProfileSectionHeader("Support") }
            // Developer Tools (Can be removed in production)
            // --------------------------------------------------
            item {
                PreferenceItem(
                    title = "Developer Dashboard",
                    subtitle = "Analyze SMS patterns & detection",
                    icon = Icons.Default.DeveloperMode,
                    onClick = { navController.navigate(Screen.DeveloperDashboard.route) }
                )
            }
            item {
                PreferenceItem(
                    title = "BETA AUDIT EXPORT",
                    subtitle = "Generate full audit package (ZIP)",
                    icon = Icons.Default.Download,
                    onClick = { auditViewModel.runBetaAudit() }
                )
            }
            // --------------------------------------------------
            item {
                PreferenceItem(
                    title = "Report a Bug",
                    subtitle = "Help us improve your experience",
                    icon = Icons.Default.BugReport,
                    onClick = { }
                )
            }
            item {
                PreferenceItem(
                    title = "About App",
                    subtitle = "Version 1.0.0",
                    icon = Icons.Default.Info,
                    onClick = { }
                )
            }

            // Logout/Exit
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorTransport.copy(alpha = 0.1f), contentColor = ColorTransport),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Logout & Switch Account", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showCurrencyPicker) {
        CurrencyPicker(
            onDismiss = { showCurrencyPicker = false },
            onSelect = { 
                viewModel.updateCurrency(it)
                showCurrencyPicker = false
            }
        )
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            authType = uiState.profile.authType,
            backupStatus = uiState.backupStatus,
            onDismiss = { showLogoutDialog = false },
            onLogout = {
                viewModel.logout {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0)
                    }
                }
            },
            onBackupAndLogout = {
                viewModel.performBackupBeforeLogout {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0)
                    }
                }
            }
        )
    }
}

@Composable
fun IdentityHeader(name: String, email: String?, authType: String, onLoginClick: () -> Unit) {
    val badgeColor = when (authType) {
        "GOOGLE" -> Color(0xFF3B82F6)
        "EMAIL" -> PrimaryAccent
        else -> ColorTransport
    }
    
    val badgeText = when (authType) {
        "GOOGLE" -> "Google Verified"
        "EMAIL" -> "Standard Account"
        else -> "Local Only (Not Backed Up)"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(SurfaceGlassBright, Color.Transparent)))
            .border(1.dp, SurfaceGlassBright, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Profile Photo / Initials
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.2f))
                    .border(2.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (!email.isNullOrBlank()) {
                    Text(email, color = TextSecondary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = badgeColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (authType == "LOCAL") {
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    authType: String,
    backupStatus: com.example.smartexpensecalendar.presentation.profile.BackupStatus,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onBackupAndLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        title = { Text("Logout & Switch Account", color = TextPrimary) },
        text = {
            Column {
                val message = when (authType) {
                    "LOCAL" -> "Are you sure? All your local data (expenses, rules) will be PERMANENTLY DELETED as it's not backed up."
                    "EMAIL" -> "You're logged in with Email. We recommend backing up your data to Google Drive before logging out."
                    "GOOGLE" -> "Logging out... We'll perform a final backup to your Google Drive to ensure no data is lost."
                    else -> "Are you sure you want to logout?"
                }
                Text(message, color = TextSecondary)
                
                if (backupStatus is com.example.smartexpensecalendar.presentation.profile.BackupStatus.InProgress) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CyanGlow)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Backing up data...", color = CyanGlow)
                    }
                }
                
                if (backupStatus is com.example.smartexpensecalendar.presentation.profile.BackupStatus.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(backupStatus.message, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            if (backupStatus !is com.example.smartexpensecalendar.presentation.profile.BackupStatus.InProgress) {
                if (authType == "GOOGLE") {
                    Button(
                        onClick = onBackupAndLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                    ) {
                        Text("Backup & Logout")
                    }
                } else if (authType == "EMAIL") {
                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = onBackupAndLogout,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Link Drive & Backup")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onLogout) {
                            Text("Logout Anyway", color = ColorTransport)
                        }
                    }
                } else {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorTransport)
                    ) {
                        Text("Delete Data & Logout")
                    }
                }
            }
        },
        dismissButton = {
            if (backupStatus !is com.example.smartexpensecalendar.presentation.profile.BackupStatus.InProgress) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        }
    )
}

@Composable
fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = CyanGlow,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
fun PreferenceItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceGlassBright),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}

@Composable
fun SyncPreferenceItem(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGlass)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceGlassBright),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Sync, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Auto-Sync SMS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Scan for new transactions in background", color = TextSecondary, fontSize = 12.sp)
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = CyanGlow.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun CurrencyPicker(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val currencies = listOf("₹", "$", "€", "£")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        title = { Text("Select Currency", color = TextPrimary) },
        text = {
            Column {
                currencies.forEach { symbol ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(symbol) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(symbol, fontSize = 20.sp, color = CyanGlow, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = when(symbol) {
                                "₹" -> "Indian Rupee"
                                "$" -> "US Dollar"
                                "€" -> "Euro"
                                else -> "British Pound"
                            },
                            color = TextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}
