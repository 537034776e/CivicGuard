package com.example.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ReportEntity
import com.example.data.ReportRepository
import com.example.service.SensorMonitorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CivicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ReportRepository(application)

    // Reactive database flow (Single Source of Truth)
    val reports: StateFlow<List<ReportEntity>> = repository.reportsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Real-time telemetry synchronized from SensorMonitorService broadcasts
    private val _lightLevel = MutableStateFlow(50f)
    val lightLevel: StateFlow<Float> = _lightLevel.asStateFlow()

    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val telemetryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SensorMonitorService.ACTION_LIGHT_UPDATE) {
                val light = intent.getFloatExtra(SensorMonitorService.EXTRA_LIGHT_VALUE, 50f)
                val battery = intent.getIntExtra(SensorMonitorService.EXTRA_BATTERY_VALUE, 100)
                _lightLevel.value = light
                _batteryLevel.value = battery
            }
        }
    }

    init {
        val filter = IntentFilter(SensorMonitorService.ACTION_LIGHT_UPDATE)
        
        // Backward-compatible registration using ContextCompat down to SDK 24
        ContextCompat.registerReceiver(
            application,
            telemetryReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        
        // Trigger cache reload on start
        refreshReports()
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun refreshReports() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            val result = repository.refreshReports()
            if (result.isFailure) {
                _errorMessage.value = "Impossibile aggiornare i dati dal server Tomcat. Cache offline attiva."
            }
            _isRefreshing.value = false
        }
    }

    fun submitReport(
        title: String,
        description: String,
        category: String,
        severity: String,
        sender: String,
        lat: Double,
        lng: Double,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.createReport(
                title = title,
                description = description,
                category = category,
                severity = severity,
                sender = sender,
                lat = lat,
                lng = lng,
                light = _lightLevel.value,
                battery = _batteryLevel.value
            )
            onComplete(result.isSuccess)
        }
    }

    fun syncOfflineReports() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val count = repository.syncOfflineReports()
            if (count > 0) {
                _errorMessage.value = "Sincronizzate $count segnalazioni offline con il server!"
            } else {
                _errorMessage.value = "Nessun dato locale in coda da caricare."
            }
            repository.refreshReports()
            _isRefreshing.value = false
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
        }
    }

    fun deleteReport(id: Int) {
        viewModelScope.launch {
            repository.deleteReport(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(telemetryReceiver)
        } catch (e: Exception) {
            // Safe ignore
        }
    }
}
