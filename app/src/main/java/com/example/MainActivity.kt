package com.example

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.service.SensorMonitorService

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sincronizza ed avvia il servizio in background per monitorare la sensoristica di sistema
        val serviceIntent = Intent(this, SensorMonitorService::class.java)
        startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Arresta il monitoraggio in background alla chiusura dell'applicazione
        val serviceIntent = Intent(this, SensorMonitorService::class.java)
        stopService(serviceIntent)
    }
}
