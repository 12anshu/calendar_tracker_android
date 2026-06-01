package com.example.smartexpensecalendar.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartexpensecalendar.R
import com.example.smartexpensecalendar.presentation.home.HomeUiEvent
import com.example.smartexpensecalendar.presentation.home.HomeViewModel
import com.example.smartexpensecalendar.ui.components.CalendarView
import com.example.smartexpensecalendar.ui.components.MonthlySummary
import com.example.smartexpensecalendar.ui.components.MonthYearPicker
import com.example.smartexpensecalendar.ui.components.NotificationBottomSheet
import com.example.smartexpensecalendar.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle as DateTextStyle
import java.util.*

import androidx.navigation.NavController
import com.example.smartexpensecalendar.ui.navigation.Screen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val syncSummary by viewModel.syncSummary.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var historicalSyncMonth by remember { mutableStateOf<YearMonth?>(null) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    // Sliding gesture state
    var swipeOffset by remember { mutableFloatStateOf(0f) }
    val draggableState = rememberDraggableState { delta ->
        swipeOffset += delta
    }

    LaunchedEffect(swipeOffset) {
        if (swipeOffset > 150f) {
            viewModel.prevMonth()
            swipeOffset = 0f
        } else if (swipeOffset < -150f) {
            viewModel.nextMonth()
            swipeOffset = 0f
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                // content is handled via event
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.ExportFile -> {
                    createDocumentLauncher.launch(event.fileName)
                }
                is HomeUiEvent.RequestHistoricalSync -> {
                    historicalSyncMonth = event.yearMonth
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(Brush.linearGradient(
//                colors = listOf(
//                    Color(0xFF020617),
//                    Color(0xFF071427),
//                    Color(0xFF0F172A)
//                )
//            )
//        )
            .background(
                Brush.verticalGradient(
                    listOf(CyanGlow.copy(alpha = 0.1f), CyanGlow.copy(alpha = 0.1f))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                FintechHeader(
                    onResetClick = { viewModel.resetAndSync() },
                    onManageRulesClick = { navController.navigate(Screen.MerchantRules.route) },
                    notificationCount = notifications.size,
                    onNotificationClick = { showNotifications = true }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        onDragStopped = { swipeOffset = 0f }
                    )
            ) {
                // Month Selector Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Box {
                            TextButton(
                                onClick = { showMonthPicker = true },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${
                                            selectedMonth.month.getDisplayName(
                                                DateTextStyle.FULL,
                                                Locale.getDefault()
                                            )
                                        } ${selectedMonth.year}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            letterSpacing = (-0.5).sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            }
                            if (showMonthPicker) {
                                MonthYearPicker(
                                    initialMonth = selectedMonth,
                                    onDismiss = { showMonthPicker = false },
                                    onConfirm = { 
                                        viewModel.setMonth(it)
                                        showMonthPicker = false 
                                    }
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                CyanGlow
                            )
                            .border(
                                1.dp,
                                PrimaryAccent.copy(alpha = 0.4f),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable(
                                enabled = !uiState.isSyncing
                            ) {
                                viewModel.syncSelectedMonth()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                        {
                            Text("Sync",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF02131F))
                        }
                    }

                Spacer(modifier = Modifier.height(12.dp))

                CalendarView(
                    yearMonth = selectedMonth,
                    expenses = expenses,
                    selectedDate = selectedDate,
                    currencySymbol = uiState.currencySymbol,
                    onDateClick = { date ->
                        selectedDate = date
                        showDetailSheet = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                MonthlySummary(
                    expenses = expenses,
                    syncSummary = syncSummary,
                    totalBudget = uiState.totalBudget,
                    currencySymbol = uiState.currencySymbol,
                    onExportCSV = { viewModel.exportCSV() },
                    onAnalyticsClick = { navController.navigate(Screen.SpendingAnalysis.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Bottom Navigation
        FintechBottomNav(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Expense Detail Sheet
        if (showDetailSheet && selectedDate != null) {
            ExpenseDetailBottomSheet(
                date = selectedDate!!,
                onDismiss = { 
                    showDetailSheet = false 
                    selectedDate = LocalDate.now()
                }
            )
        }

        if (showNotifications) {
            NotificationBottomSheet(
                notifications = notifications,
                onClearAll = { viewModel.clearNotifications() },
                onDismiss = { showNotifications = false }
            )
        }
    }
}

@Composable
fun FintechHeader(
    onResetClick: () -> Unit,
    onManageRulesClick: () -> Unit = {},
    notificationCount: Int = 0,
    onNotificationClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Row: Menu, Logo, and Notifications
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(BackgroundEnd)
                ) {
                    DropdownMenuItem(
                        text = { Text("Auto-Categorization Rules", color = TextPrimary) },
                        onClick = {
                            showMenu = false
                            onManageRulesClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = CyanGlow)
                        }
                    )
                    HorizontalDivider(color = SurfaceGlassBright)
                    DropdownMenuItem(
                        text = { Text("Reset Current Month & Sync", color = Color.Red) },
                        onClick = {
                            showMenu = false
                            onResetClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Red)
                        }
                    )
                }
            }

            // Logo in the center of the top row
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(
                                containerColor = ColorTransport,
                                contentColor = Color.White
                            ) {
                                Text(notificationCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Aligned Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Smart ",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text = "Expense Tracker",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = CyanGlow,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )
        }
    }
}

@Composable
fun FintechBottomNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 20.dp)
            .fillMaxWidth()
            .height(82.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FintechNavItem(Icons.Default.Home, "Home", true) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            FintechNavItem(Icons.AutoMirrored.Filled.ReceiptLong, "Transactions", isSelected = false) {
                navController.navigate(Screen.Transactions.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            FintechNavItem(Icons.Default.AutoGraph, "Insights", isSelected = false) {
                navController.navigate(Screen.Insights.route)
            }
            FintechNavItem(Icons.Default.AccountBalanceWallet, "Budget", false) {
                navController.navigate(Screen.Budget.route)
            }
            FintechNavItem(Icons.Default.Person, "Profile", isSelected = false) {
                navController.navigate(Screen.Profile.route)
            }
        }
    }
}

@Composable
fun FintechNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { onClick() }
    ) {

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    if (isSelected)
                        Color(0xFF14B8A6).copy(alpha = 0.18f)
                    else
                        Color.Transparent
                )
                .padding(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected)
                    Color(0xFF2DD4BF)
                else
                    Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (isSelected)
                Color(0xFF2DD4BF)
            else
                Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = if (isSelected)
                FontWeight.Bold
            else
                FontWeight.Medium
        )
    }
}