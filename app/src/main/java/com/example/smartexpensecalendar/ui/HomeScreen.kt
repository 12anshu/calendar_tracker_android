package com.example.smartexpensecalendar.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val processedCount by viewModel.processedSMSCount.collectAsState()
    val context = LocalContext.current
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var historicalSyncMonth by remember { mutableStateOf<YearMonth?>(null) }

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
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { viewModel.prevMonth() }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev")
                            }
                            Text(
                                text = "${selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedMonth.year}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            IconButton(onClick = { viewModel.nextMonth() }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
                            }
                        }
                    }
                )
                /*
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search category or merchant") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                */
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CalendarView(
                yearMonth = selectedMonth,
                expenses = expenses,
                onDateClick = { date ->
                    selectedDate = date
                    showDetailSheet = true
                },
                modifier = Modifier.weight(1f)
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
                        text = "Syncing SMS... ($processedCount found)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            HorizontalDivider()

            MonthlySummary(
                expenses = expenses,
                onExportCSV = { viewModel.exportCSV() },
                onExportJSON = { viewModel.exportJSON() },
                onImportJSON = { viewModel.triggerImport() },
                modifier = Modifier.height(250.dp)
            )
        }
    }

    if (showDetailSheet && selectedDate != null) {
        ExpenseDetailBottomSheet(
            date = selectedDate!!,
            onDismiss = { showDetailSheet = false }
        )
    }
}
