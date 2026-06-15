package com.example.data.api

import com.example.data.ReportEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReportDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val severity: String,
    val sender: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val ambientLight: Float,
    val batteryLevel: Int
) {
    fun toEntity(isSynced: Boolean = true): ReportEntity {
        return ReportEntity(
            id = id,
            title = title,
            description = description,
            category = category,
            severity = severity,
            sender = sender,
            timestamp = timestamp,
            isSynced = isSynced,
            latitude = latitude,
            longitude = longitude,
            ambientLight = ambientLight,
            batteryLevel = batteryLevel
        )
    }

    companion object {
        fun fromEntity(entity: ReportEntity): ReportDto {
            return ReportDto(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                category = entity.category,
                severity = entity.severity,
                sender = entity.sender,
                timestamp = entity.timestamp,
                latitude = entity.latitude,
                longitude = entity.longitude,
                ambientLight = entity.ambientLight,
                batteryLevel = entity.batteryLevel
            )
        }
    }
}
