package com.example.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CivicApi {
    @GET("reports")
    suspend fun getReports(): List<ReportDto>

    @POST("reports")
    suspend fun submitReport(@Body report: ReportDto): ReportDto
}
