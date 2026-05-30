package com.example.smartexpensecalendar.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartexpensecalendar.presentation.home.HomeUiEvent
import com.example.smartexpensecalendar.presentation.home.HomeViewModel
import com.example.smartexpensecalendar.ui.components.CalendarView
import com.example.smartexpensecalendar.ui.components.MonthlySummary
import com.example.smartexpensecalendar.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle as DateTextStyle
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val syncSummary by viewModel.syncSummary.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var historicalSyncMonth by remember { mutableStateOf<YearMonth?>(null) }
    var showMonthPicker by remember { mutableStateOf(false) }

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
            .background(Brush.linearGradient(
                colors = listOf(
                    Color(0xFF020617),
                    Color(0xFF071427),
                    Color(0xFF0F172A)
                )
            )
        )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                FintechHeader()
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
                            DropdownMenu(
                                expanded = showMonthPicker,
                                onDismissRequest = { showMonthPicker = false },
                                modifier = Modifier.background(SurfaceGlass)
                            ) {
                                val current = YearMonth.now()
                                (-12..12).forEach { offset ->
                                    val month = current.plusMonths(offset.toLong())
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${
                                                    month.month.getDisplayName(
                                                        DateTextStyle.FULL,
                                                        Locale.getDefault()
                                                    )
                                                } ${month.year}", color = TextPrimary
                                            )
                                        },
                                        onClick = {
                                            viewModel.setMonth(month)
                                            showMonthPicker = false
                                        }
                                    )
                                }
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
                    onExportCSV = { viewModel.exportCSV() },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Bottom Navigation
        FintechBottomNav(modifier = Modifier.align(Alignment.BottomCenter))

        // Expense Detail Sheet
        if (showDetailSheet && selectedDate != null) {
            ExpenseDetailBottomSheet(
                date = selectedDate!!,
                onDismiss = { showDetailSheet = false }
            )
        }
    }
}

@Composable
fun FintechHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        IconButton(
            onClick = { },
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
                modifier = Modifier.size(26.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        ) {
            Text(
                text = "Smart Expense Calendar",
                style = MaterialTheme.typography.headlineMedium.copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFF2DD4BF)
                        )
                    ),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8).sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Track. Understand. Save Smarter.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.3.sp
                )
            )
        }

        Box {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FintechBottomNav(modifier: Modifier = Modifier) {

    Box(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .fillMaxWidth()
            .height(82.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(32.dp)
            )
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            FintechNavItem(Icons.Default.Home, "Home", true)
            FintechNavItem(Icons.AutoMirrored.Filled.List, "Transactions", false)
            FintechNavItem(Icons.Default.Info, "Analytics", false)
            FintechNavItem(Icons.Default.Settings, "Budgets", false)
            FintechNavItem(Icons.Default.Person, "Profile", false)
        }
    }
}

@Composable
fun FintechNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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