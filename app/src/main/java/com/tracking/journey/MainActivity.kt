package com.tracking.journey

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartStop: Button
    private lateinit var tvStatus: TextView
    private var tracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartStop = findViewById(R.id.btnStartStop)
        tvStatus = findViewById(R.id.tvStatus)

        btnStartStop.setOnClickListener {
            if (!tracking) {
                if (checkPermissions()) {
                    startTracking()
                } else {
                    requestPermissions()
                }
            } else {
                stopTracking()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        permissionLauncher.launch(permissions)
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) {
                startTracking()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startTracking() {
        val serviceIntent = Intent(this, JourneyService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        tracking = true
        btnStartStop.text = "Stop Journey"
        tvStatus.text = "Status: Tracking"
    }

    private fun stopTracking() {
        val serviceIntent = Intent(this, JourneyService::class.java)
        stopService(serviceIntent)
        tracking = false
        btnStartStop.text = "Start Journey"
        tvStatus.text = "Status: Not Tracking"
    }
}
