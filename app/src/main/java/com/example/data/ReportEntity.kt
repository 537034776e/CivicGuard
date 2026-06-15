package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val severity: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val latitude: Double = 41.9028, // Default Roma
    val longitude: Double = 12.4964,
    val ambientLight: Float = 0f,
    val batteryLevel: Int = 100
) : Serializable
