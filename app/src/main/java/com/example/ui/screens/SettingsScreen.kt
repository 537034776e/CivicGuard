package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.viewmodel.CivicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CivicViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("civicguard_prefs", Context.MODE_PRIVATE) }
    val scrollState = rememberScrollState()

    // SharedPreferences State management
    var isMockMode by remember { mutableStateOf(sharedPrefs.getBoolean("pref_mock_mode", true)) }
    var serverIp by remember { mutableStateOf(sharedPrefs.getString("pref_server_ip", "10.0.2.2") ?: "10.0.2.2") }
    var serverPort by remember { mutableStateOf(sharedPrefs.getString("pref_server_port", "8080") ?: "8080") }
    var serverApp by remember { mutableStateOf(sharedPrefs.getString("pref_server_app", "CivicServer") ?: "CivicServer") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurazione Server & App", fontWeight = FontWeight.Bold) },
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
            // Section: Connettività
            Text(
                text = "Interfaccia Server Tomcat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Switch to Toggle Offline Simulation vs Actual API Integration
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isMockMode) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modalità Demo Semplificata",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Se attiva, le chiamate HTTP catturano i report internamente simulando Tomcat, idoneo per test offline immediati.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isMockMode,
                        onCheckedChange = { checked ->
                            isMockMode = checked
                            sharedPrefs.edit().putBoolean("pref_mock_mode", checked).apply()
                            Toast.makeText(context, if (checked) "Inizializzata Simulazione!" else "Connessione diretta attiva!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("switch_mock_mode")
                    )
                }
            }

            // Server Parameters (Editable only when Demo Mode is FALSE)
            AnimatedVisibility(visible = !isMockMode) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Parametri Host Tomcat",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = serverIp,
                        onValueChange = {
                            serverIp = it
                            sharedPrefs.edit().putString("pref_server_ip", it).apply()
                        },
                        label = { Text("IP Server") },
                        placeholder = { Text("Es: 10.0.2.2 o 192.168.1.100") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_server_ip"),
                        leadingIcon = { Icon(imageVector = Icons.Default.Computer, contentDescription = null) },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = serverPort,
                        onValueChange = {
                            serverPort = it
                            sharedPrefs.edit().putString("pref_server_port", it).apply()
                        },
                        label = { Text("Porta Server") },
                        placeholder = { Text("Es: 8080") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_server_port"),
                        leadingIcon = { Icon(imageVector = Icons.Default.NetworkPing, contentDescription = null) },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = serverApp,
                        onValueChange = {
                            serverApp = it
                            sharedPrefs.edit().putString("pref_server_app", it).apply()
                        },
                        label = { Text("Nome Applicazione Servlet") },
                        placeholder = { Text("Es: CivicServer") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_server_app"),
                        leadingIcon = { Icon(imageVector = Icons.Default.Shortcut, contentDescription = null) },
                        singleLine = true
                    )

                    // Real Connection URL Info Box
                    val fullUrl = "http://$serverIp:$serverPort/$serverApp/reports"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "URL Endpoint di Riferimento:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = fullUrl,
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Divider()

            // Section: Cache / Database local management
            Text(
                text = "Archiviazione Locale & Cache",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = {
                    viewModel.clearCache()
                    Toast.makeText(context, "Database locale vuoto! Svuotamento effettuato.", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("clear_cache_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancella Cache Interna (Room DB)")
                }
            }

            Divider()

            // Instructional Academic Info helper block
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guida d'Esame: Integrazione Tomcat",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Questo client Android è codificato per connettersi a qualsiasi servlet Java in ascolto su Tomcat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Configura una Servlet esposta all'endpoint /reports.\n" +
                               "2. Implementa il metodo doGet (restituisce un array JSON di report) e doPost (riceve un oggetto JSON e lo memorizza).\n" +
                               "3. Disattiva la 'Modalità Demo Semplificata' qui sopra, immetti l'indirizzo IP del tuo PC locale (usando 10.0.2.2 sull'emulatore) e testa la piena comunicazione client-server!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
