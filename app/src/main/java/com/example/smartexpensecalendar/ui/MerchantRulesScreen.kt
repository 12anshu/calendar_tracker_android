package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.presentation.rules.MerchantRulesViewModel
import com.example.smartexpensecalendar.presentation.rules.MerchantRule
import com.example.smartexpensecalendar.presentation.rules.RuleSource
import com.example.smartexpensecalendar.ui.components.CategoryIconView
import com.example.smartexpensecalendar.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantRulesScreen(
    navController: NavController,
    viewModel: MerchantRulesViewModel = hiltViewModel()
) {
    val rules by viewModel.rules.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val unmappedMerchants by viewModel.unmappedMerchants.collectAsState()
    
    var editingRule by remember { mutableStateOf<MerchantRule?>(null) }
    var showAddRuleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorization Rules", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddRuleDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Rule", tint = CyanGlow)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundStart)
            )
        },
        containerColor = BackgroundStart
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            Text(
                text = "Manage how merchants from your SMS are categorized. Active merchants from your history are shown first.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            if (rules.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanGlow)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Group by source for better visual organization
                    val activeGroup = rules.filter { it.source == RuleSource.ACTIVE }
                    val customGroup = rules.filter { it.source == RuleSource.CUSTOM }
                    val systemGroup = rules.filter { it.source == RuleSource.SYSTEM }

                    if (activeGroup.isNotEmpty()) {
                        item { GroupHeader("Detected from SMS") }
                        items(activeGroup) { rule ->
                            RuleItem(rule, { viewModel.deleteRule(rule) }, { editingRule = rule })
                        }
                    }

                    if (customGroup.isNotEmpty()) {
                        item { GroupHeader("Your Custom Rules") }
                        items(customGroup) { rule ->
                            RuleItem(rule, { viewModel.deleteRule(rule) }, { editingRule = rule })
                        }
                    }

                    if (systemGroup.isNotEmpty()) {
                        item { GroupHeader("System Defaults") }
                        items(systemGroup) { rule ->
                            RuleItem(rule, { viewModel.deleteRule(rule) }, { editingRule = rule })
                        }
                    }
                }
            }
        }
    }

    if (editingRule != null) {
        RuleEditDialog(
            rule = editingRule!!,
            categories = categories,
            onDismiss = { editingRule = null },
            onSave = { newCat ->
                viewModel.updateRule(editingRule!!.keyword, newCat)
                editingRule = null
            }
        )
    }

    if (showAddRuleDialog) {
        AddRuleDialog(
            merchants = unmappedMerchants,
            categories = categories,
            onDismiss = { showAddRuleDialog = false },
            onSave = { merchant, category ->
                viewModel.updateRule(merchant, category)
                showAddRuleDialog = false
            }
        )
    }
}

@Composable
fun GroupHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = CyanGlow,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun RuleItem(rule: MerchantRule, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceGlass)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            CategoryIconView(category = rule.category, size = 38.dp, iconSize = 18.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rule.keyword.uppercase(), 
                        color = TextPrimary, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    if (rule.source == RuleSource.SYSTEM) {
                        RuleTag("System", SurfaceGlassBright)
                    } else if (rule.source == RuleSource.ACTIVE) {
                        RuleTag("${rule.frequency} txns", PrimaryAccent.copy(alpha = 0.2f))
                    }
                }
                Text(rule.category, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
            }
        }
        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Tune, contentDescription = "Configure", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            if (rule.source == RuleSource.CUSTOM) {
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Rule", tint = ColorTransport, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun RuleTag(text: String, color: Color) {
    Surface(
        modifier = Modifier.padding(start = 6.dp),
        color = color,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text, 
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
            fontSize = 7.sp, 
            color = TextPrimary,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun RuleEditDialog(
    rule: MerchantRule,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(rule.category) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        title = { Text("Categorize ${rule.keyword.uppercase()}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Assign a permanent category to this merchant. All current and future transactions will be updated.", 
                    color = TextSecondary, 
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SurfaceGlassBright)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            CategoryIconView(category = selectedCategory, size = 24.dp, iconSize = 14.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(selectedCategory, fontSize = 14.sp)
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(BackgroundEnd)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CategoryIconView(category = cat, size = 24.dp, iconSize = 14.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(cat, color = TextPrimary) 
                                    }
                                },
                                onClick = { selectedCategory = cat; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedCategory) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Set Rule", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddRuleDialog(
    merchants: List<String>,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var selectedMerchant by remember { mutableStateOf(if (merchants.isNotEmpty()) merchants.first() else "") }
    var selectedCategory by remember { mutableStateOf(if (categories.isNotEmpty()) categories.first() else "") }
    var merchantExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundEnd,
        title = { Text("Add New Rule", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Select a merchant keyword from your SMS and assign its permanent category.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Merchant Picker
                Text("Merchant Keyword", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedButton(
                        onClick = { merchantExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SurfaceGlassBright))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (selectedMerchant.isBlank()) "No merchants found" else selectedMerchant.uppercase(), fontSize = 14.sp, maxLines = 1)
                            Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(20.dp))
                        }
                    }
                    DropdownMenu(expanded = merchantExpanded, onDismissRequest = { merchantExpanded = false }, modifier = Modifier.background(BackgroundEnd)) {
                        if (merchants.isEmpty()) {
                            DropdownMenuItem(text = { Text("No unmapped merchants detected", color = TextSecondary) }, onClick = { })
                        }
                        merchants.forEach { m ->
                            DropdownMenuItem(text = { Text(m.uppercase(), color = TextPrimary) }, onClick = { selectedMerchant = m; merchantExpanded = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Category Picker
                Text("Assigned Category", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SurfaceGlassBright))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CategoryIconView(category = selectedCategory, size = 24.dp, iconSize = 14.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(selectedCategory, fontSize = 14.sp)
                            }
                            Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(20.dp))
                        }
                    }
                    DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }, modifier = Modifier.background(BackgroundEnd)) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CategoryIconView(category = cat, size = 24.dp, iconSize = 14.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(cat, color = TextPrimary) 
                                    }
                                },
                                onClick = { selectedCategory = cat; categoryExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (selectedMerchant.isNotBlank()) onSave(selectedMerchant, selectedCategory) },
                enabled = selectedMerchant.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = BackgroundStart),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Create Rule", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
