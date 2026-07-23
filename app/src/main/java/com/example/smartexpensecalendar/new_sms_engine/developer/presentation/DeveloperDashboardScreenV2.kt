package com.example.smartexpensecalendar.new_sms_engine.developer.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.core.designsystem.theme.*
import com.example.smartexpensecalendar.new_sms_engine.developer.presentation.components.DashboardSummarySection
import com.example.smartexpensecalendar.new_sms_engine.developer.presentation.components.DeveloperSmsCardV2
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperDashboardScreenV2(
    navController: NavController,
    viewModel: DeveloperDashboardViewModelV2 = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    
    // Track expanded months (collapsed by default)
    val expandedMonths = rememberSaveable { mutableStateOf(setOf<String>()) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            viewModel.writeCsvToUri(context, it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is DeveloperDashboardEffect.ExportCsv -> {
                    createDocumentLauncher.launch(effect.fileName)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New SMS Engine Lab",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::exportToCsv) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export CSV",
                            tint = CyanGlow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundStart)
            )
        },
        containerColor = BackgroundStart
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (progress < 1f) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = CyanGlow,
                    trackColor = SurfaceGlass
                )
            }

            // Developer Search Bar
            TextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Search Pattern, Evidence, Token, Merchant...", color = TextSecondary, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .background(SurfaceGlass, RoundedCornerShape(12.dp)),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    DashboardSummarySection(
                        summary = uiState.summary,
                        selectedFilters = uiState.selectedFilters,
                        onFilterClick = viewModel::onFilterClicked
                    )
                }

                if (uiState.selectedFilters.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Active Filters", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    text = uiState.selectedFilters.joinToString(" + ") { it.displayName() },
                                    color = CyanGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            TextButton(onClick = viewModel::clearFilter) {
                                Text("Clear All", color = ColorTransport)
                            }
                        }
                    }
                }

                uiState.groupedFilteredSms.forEach { (month, smsList) ->
                    val isExpanded = expandedMonths.value.contains(month)
                    
                    item(key = "header_$month") {
                        MonthHeader(
                            month = month,
                            count = smsList.size,
                            isCollapsed = !isExpanded,
                            onToggle = {
                                expandedMonths.value = if (isExpanded) {
                                    expandedMonths.value - month
                                } else {
                                    expandedMonths.value + month
                                }
                            }
                        )
                    }

                    if (isExpanded) {
                        items(
                            items = smsList,
                            key = { it.id }
                        ) { sms ->
                            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                                DeveloperSmsCardV2(sms = sms)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthHeader(
    month: String,
    count: Int,
    isCollapsed: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        color = BackgroundStart.copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCollapsed) Icons.AutoMirrored.Filled.KeyboardArrowRight else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = month,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "($count)",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
