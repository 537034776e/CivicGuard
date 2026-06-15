package com.example.data.api

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

class MockInterceptor(private val context: Context) : Interceptor {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listAdapter = moshi.adapter<List<ReportDto>>(Types.newParameterizedType(List::class.java, ReportDto::class.java))
    private val dtoAdapter = moshi.adapter(ReportDto::class.java)

    // In-memory list of reports to mock the Tomcat server's database state
    private val serverReports = mutableListOf<ReportDto>(
        ReportDto(1, "Buca profonda in Via del Corso", "Sulla carreggiata destra direzione Piazza Venezia, profonda circa 10cm. Pericolo per motocicli.", "Buchi stradali", "Alta", "Marco Rossi", System.currentTimeMillis() - 86400000, 41.9028, 12.4964, 150f, 92),
        ReportDto(2, "Lampione spento Villa Borghese", "Intera fila di lampioni spenta vicino all'ingresso di Porta Pinciana.", "Illuminazione", "Media", "Sofia Bianchi", System.currentTimeMillis() - 43200000, 41.9128, 12.4864, 5f, 85),
        ReportDto(3, "Rifiuti ingombranti non ritirati", "Un divano abbandonato sul marciapiede davanti all'ingresso della scuola.", "Rifiuti", "Alta", "Giovanni Verdi", System.currentTimeMillis() - 21600000, 41.9050, 12.5000, 200f, 78)
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val sharedPrefs = context.getSharedPreferences("civicguard_prefs", Context.MODE_PRIVATE)
        val isMockMode = sharedPrefs.getBoolean("pref_mock_mode", true)

        if (!isMockMode) {
            // Call actual server
            return chain.proceed(request)
        }

        // Simulate moderate latency of 800ms
        try {
            Thread.sleep(800)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Intercept GET reports
        if (request.method == "GET" && url.endsWith("reports")) {
            val json = listAdapter.toJson(serverReports)
            return Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(json.toResponseBody("application/json".toMediaTypeOrNull()))
                .addHeader("content-type", "application/json")
                .build()
        }

        // Intercept POST reports
        if (request.method == "POST" && url.endsWith("reports")) {
            val body = request.body
            val buffer = okio.Buffer()
            body?.writeTo(buffer)
            val requestJson = buffer.readUtf8()

            try {
                val inputDto = dtoAdapter.fromJson(requestJson)
                if (inputDto != null) {
                    val id = serverReports.size + 1
                    val newDto = inputDto.copy(id = id, timestamp = System.currentTimeMillis())
                    serverReports.add(0, newDto) // Insert at top

                    val responseJson = dtoAdapter.toJson(newDto)
                    return Response.Builder()
                        .code(201)
                        .message("Created")
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .body(responseJson.toResponseBody("application/json".toMediaTypeOrNull()))
                        .addHeader("content-type", "application/json")
                        .build()
                }
            } catch (e: Exception) {
                return Response.Builder()
                    .code(400)
                    .message("Bad Request: ${e.message}")
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
                    .build()
            }
        }

        return Response.Builder()
            .code(404)
            .message("Not Found")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
