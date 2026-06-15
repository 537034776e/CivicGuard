package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.CivicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    viewModel: CivicViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Retrieve live telemetry capturing values
    val currentLight by viewModel.lightLevel.collectAsStateWithLifecycle()
    val currentBattery by viewModel.batteryLevel.collectAsStateWithLifecycle()

    // Form states
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sender by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Buchi stradali") }
    var selectedSeverity by remember { mutableStateOf("Media") }
    
    // GPS selection states
    var latitudeStr by remember { mutableStateOf("41.9028") }  // Redundant with Roma Centro default
    var longitudeStr by remember { mutableStateOf("12.4964") }
    
    var isSubmitting by remember { mutableStateOf(false) }

    val categories = listOf("Buchi stradali", "Illuminazione", "Rifiuti", "Verde Urbano", "Altro")
    val severities = listOf("Bassa", "Media", "Alta")

    // Dropdown menus states
    var categoryExpanded by remember { mutableStateOf(false) }
    var severityExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invia Segnalazione", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Title: Compila Dati
            Text(
                text = "Dettagli Segnalazione",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo Segnalazione") },
                placeholder = { Text("Es: Buca d'acqua profonda via Rossi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_title"),
                leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = null) },
                singleLine = true
            )

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrizione") },
                placeholder = { Text("Fornisci dettagli sul problema rilevato...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("input_description"),
                leadingIcon = { Icon(imageVector = Icons.Default.Description, contentDescription = null) },
                maxLines = 4
            )

            // Sender Input (Name)
            OutlinedTextField(
                value = sender,
                onValueChange = { sender = it },
                label = { Text("Tuo Nome o Username") },
                placeholder = { Text("Es: Luigi Verdi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_sender"),
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                singleLine = true
            )

            // Dropdown Selector: Category
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("dropdown_api_category"),
                    leadingIcon = { Icon(imageVector = Icons.Default.Category, contentDescription = null) }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown Selector: Severity
            ExposedDropdownMenuBox(
                expanded = severityExpanded,
                onExpandedChange = { severityExpanded = !severityExpanded }
            ) {
                OutlinedTextField(
                    value = "Priorità " + selectedSeverity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priorità / Urgenza") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = severityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("dropdown_severity"),
                    leadingIcon = { Icon(imageVector = Icons.Default.PriorityHigh, contentDescription = null) }
                )
                ExposedDropdownMenu(
                    expanded = severityExpanded,
                    onDismissRequest = { severityExpanded = false }
                ) {
                    severities.forEach { severity ->
                        DropdownMenuItem(
                            text = { Text(severity) },
                            onClick = {
                                selectedSeverity = severity
                                severityExpanded = false
                            }
                        )
                    }
                }
            }

            Divider()

            // Locality GPS Section
            Text(
                text = "Geolocalizzazione (Coordinate GPS)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = latitudeStr,
                    onValueChange = { latitudeStr = it },
                    label = { Text("Latitudine") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_latitude"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = longitudeStr,
                    onValueChange = { longitudeStr = it },
                    label = { Text("Longitudine") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_longitude"),
                    singleLine = true
                )
            }

            // Quick City hot buttons (highly visual & helpful shortcuts for presentation evaluations)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Posizioni Predefinite:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            latitudeStr = "41.9028"
                            longitudeStr = "12.4964"
                        },
                        label = { Text("Roma") }
                    )
                    AssistChip(
                        onClick = {
                            latitudeStr = "45.4642"
                            longitudeStr = "9.1900"
                        },
                        label = { Text("Milano") }
                    )
                    AssistChip(
                        onClick = {
                            latitudeStr = "40.8518"
                            longitudeStr = "14.2681"
                        },
                        label = { Text("Napoli") }
                    )
                    AssistChip(
                        onClick = {
                            latitudeStr = "43.7696"
                            longitudeStr = "11.2558"
                        },
                        label = { Text("Firenze") }
                    )
                }
            }

            // Interactive dynamic Telemetry Stamping section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Dati Telemetrici Catturati nel Report",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "I seguenti sensori saranno sigillati nel pacchetto JSON inviato al server, utili per certificare le caratteristiche ambientali della rilevazione:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LightMode, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Luce: ${currentLight.toInt()} lx", style = MaterialTheme.typography.labelSmall)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Batteria: $currentBattery%", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action: Invia Report (Saves locally and attempts immediate HTTP upload)
            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank()) {
                        Toast.makeText(context, "Inserisci Titolo e Descrizione per procedere", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val finalSender = if (sender.isBlank()) "Segnalatore Anonimo" else sender
                    val latVal = latitudeStr.toDoubleOrNull() ?: 41.9028
                    val lngVal = longitudeStr.toDoubleOrNull() ?: 12.4964

                    isSubmitting = true
                    viewModel.submitReport(
                        title = title,
                        description = description,
                        category = selectedCategory,
                        severity = selectedSeverity,
                        sender = finalSender,
                        lat = latVal,
                        lng = lngVal
                    ) { success ->
                        isSubmitting = false
                        if (success) {
                            Toast.makeText(context, "Segnalazione salvata!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Offline scenario
                            Toast.makeText(context, "Offline: Segnalazione salvata in cache locale!", Toast.LENGTH_LONG).show()
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_report_button"),
                enabled = !isSubmitting,
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Invia")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Invia Segnalazione",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
