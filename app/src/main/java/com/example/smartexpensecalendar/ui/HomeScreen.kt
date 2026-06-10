package com.example.smartexpensecalendar.ui

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.animation.core.animate
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
import com.example.smartexpensecalendar.ui.components.PremiumFeatureCard
import com.example.smartexpensecalendar.core.designsystem.theme.*
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle as DateTextStyle
import java.util.*

import androidx.navigation.NavController
import com.example.smartexpensecalendar.ui.components.AppLogoText
import com.example.smartexpensecalendar.ui.components.SyncProgressCard
import com.example.smartexpensecalendar.ui.components.FintechBottomNav
import com.example.smartexpensecalendar.ui.navigation.Screen

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val syncSummary by viewModel.syncSummary.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val previousMonthTotal by viewModel.previousMonthTotal.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var historicalSyncMonth by remember { mutableStateOf<YearMonth?>(null) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var swipeOffset by remember { mutableFloatStateOf(0f) }

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
                    onNotificationClick = { showNotifications = true },
                    onUpgradeClick = { navController.navigate(Screen.Subscription.route) }
                )
            },
            bottomBar = {
                FintechBottomNav(navController = navController)
            }
        ) { padding ->
            BoxWithConstraints(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                val isSmallScreen = maxHeight < 600.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = swipeOffset
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    val threshold = size.width / 4f // Dynamic threshold
                                    if (swipeOffset > threshold) {
                                        // Dragged from Left to Right (positive offset) -> Previous Month
                                        viewModel.prevMonth()
                                    } else if (swipeOffset < -threshold) {
                                        // Dragged from Right to Left (negative offset) -> Next Month
                                        viewModel.nextMonth()
                                    }
                                    swipeOffset = 0f
                                    /*
                                    coroutineScope.launch {
                                        animate(
                                            initialValue = swipeOffset,
                                            targetValue = 0f,
                                            animationSpec = androidx.compose.animation.core.spring(
                                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                                                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                                            )
                                        ) { value, _ ->
                                            swipeOffset = value
                                        }
                                    }
                                    */
                                },
                                onDragCancel = {
                                    swipeOffset = 0f
                                    /*
                                    coroutineScope.launch {
                                        animate(
                                            initialValue = swipeOffset,
                                            targetValue = 0f
                                        ) { value, _ ->
                                            swipeOffset = value
                                        }
                                    }
                                    */
                                },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    swipeOffset += dragAmount
                                }
                            )
                        }
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
                                    Brush.verticalGradient(
                                        listOf(CyanGlow.copy(alpha = 0.15f), Color.Transparent)
                                    )
                                )
                                .border(
                                    1.dp,
                                    PrimaryAccent.copy(alpha = 0.4f),
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable(enabled = !uiState.isSyncing) {
                                    viewModel.syncSelectedMonth()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (uiState.isSyncing) "Syncing..." else "Sync",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (uiState.isSyncing) CyanGlow.copy(alpha = 0.5f) else CyanGlow,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    if (!isSmallScreen) {
                        Spacer(modifier = Modifier.height(12.dp))
                        PremiumFeatureCard(
                            onClick = { navController.navigate(Screen.Subscription.route) },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

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
                        selectedMonth = selectedMonth,
                        previousMonthTotal = previousMonthTotal,
                        syncSummary = syncSummary,
                        totalBudget = uiState.totalBudget,
                        currencySymbol = uiState.currencySymbol,
                        onExportCSV = { viewModel.exportCSV() },
                        onAnalyticsClick = { navController.navigate(Screen.SpendingAnalysis.route) },
                        onBudgetClick = { navController.navigate(Screen.Budget.route) },
                        modifier = Modifier.weight(1f)
                    )

                    if (isSmallScreen) {
                        PremiumFeatureCard(
                            onClick = { navController.navigate(Screen.Subscription.route) },
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(82.dp)) // Nav height
                    }
                }
            }
        }

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

        // Sync Progress Overlay
        AnimatedVisibility(
            visible = uiState.isSyncing,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(enabled = false) {}, // Block interaction with background
                contentAlignment = Alignment.Center
            ) {
                SyncProgressCard(
                    modifier = Modifier
                        .padding(32.dp)
                        .shadow(24.dp, RoundedCornerShape(24.dp)),
                    statusText = "Scanned ${uiState.totalRead} SMS, found ${uiState.expensesFound} expenses"
                )
            }
        }
    }
}

@Composable
fun FintechHeader(
    onResetClick: () -> Unit,
    onManageRulesClick: () -> Unit = {},
    notificationCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    onUpgradeClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Row: Menu, Upgrade button, and Notifications
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
//            Image(
//                painter = painterResource(id = R.mipmap.ic_launcher),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(RoundedCornerShape(12.dp))
//            )

            // Upgrade Badge

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(CyanGlow.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
                    .border(
                        1.dp,
                        PrimaryAccent.copy(alpha = 0.4f),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable{
                        onUpgradeClick()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.MilitaryTech,
                        contentDescription = "SMART Premium",
                        tint = PremiumGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        "Upgrade",
                        color = CyanGlow,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

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

        Spacer(modifier = Modifier.height(8.dp))
        // Center Aligned Title Row
        AppLogoText(
            textStyle = MaterialTheme.typography.headlineLarge,
            showTagline = false
        )
    }
}
