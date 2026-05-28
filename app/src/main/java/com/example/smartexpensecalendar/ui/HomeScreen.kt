package com.example.smartexpensecalendar.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartexpensecalendar.presentation.home.HomeUiEvent
import com.example.smartexpensecalendar.presentation.home.HomeViewModel
import com.example.smartexpensecalendar.ui.components.CalendarView
import com.example.smartexpensecalendar.ui.components.MonthlySummary
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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

    var exportContent by remember { mutableStateOf("") }
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain") // Type updated dynamically
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(exportContent.toByteArray())
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val json = inputStream.bufferedReader().use { reader -> reader.readText() }
                viewModel.importJSON(json)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.ExportFile -> {
                    exportContent = event.content
                    createDocumentLauncher.launch(event.fileName)
                }
                is HomeUiEvent.TriggerImport -> {
                    openDocumentLauncher.launch(arrayOf("application/json"))
                }
                is HomeUiEvent.ShowError -> {
                    // Show snackbar or toast
                }
                is HomeUiEvent.RequestHistoricalSync -> {
                    historicalSyncMonth = event.yearMonth
                }
            }
        }
    }

    if (historicalSyncMonth != null) {
        AlertDialog(
            onDismissRequest = { 
                historicalSyncMonth = null 
                viewModel.dismissHistoricalSync()
            },
            title = { Text("Sync SMS History") },
            text = { Text("Do you want to scan SMS for ${historicalSyncMonth?.month} ${historicalSyncMonth?.year} to find expenses?") },
            confirmButton = {
                TextButton(onClick = {
                    historicalSyncMonth?.let { viewModel.confirmHistoricalSync(it) }
                    historicalSyncMonth = null
                }) {
                    Text("Sync Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    historicalSyncMonth = null 
                    viewModel.dismissHistoricalSync()
                }) {
                    Text("Skip")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Smart Expense Calendar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box {
                        TextButton(
                            onClick = { showMonthPicker = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "${selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedMonth.year}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMonthPicker,
                            onDismissRequest = { showMonthPicker = false }
                        ) {
                            val current = YearMonth.now()
                            (-12..12).forEach { offset ->
                                val month = current.plusMonths(offset.toLong())
                                DropdownMenuItem(
                                    text = { 
                                        Text("${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}") 
                                    },
                                    onClick = {
                                        viewModel.setMonth(month)
                                        showMonthPicker = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.syncSelectedMonth() },
                        enabled = !uiState.isSyncing,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Sync", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { swipeOffset = 0f }
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarView(
                    yearMonth = selectedMonth,
                    expenses = expenses,
                    onDateClick = { date ->
                        selectedDate = date
                        showDetailSheet = true
                    },
                    modifier = Modifier.wrapContentHeight() // Allow it to only take needed space
                )

                if (uiState.isSyncing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { uiState.syncProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Syncing SMS... (${uiState.totalRead} read, ${uiState.expensesFound} expenses found)",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                HorizontalDivider()

                MonthlySummary(
                    expenses = expenses,
                    syncSummary = syncSummary,
                    onExportCSV = { viewModel.exportCSV() },
                    onExportJSON = { viewModel.exportJSON() },
                    onImportJSON = { viewModel.triggerImport() },
                    modifier = Modifier.weight(1f) // Give it remaining space
                )
            }
        }
    }

    if (showDetailSheet && selectedDate != null) {
        ExpenseDetailBottomSheet(
            date = selectedDate!!,
            onDismiss = { showDetailSheet = false }
        )
    }
}
