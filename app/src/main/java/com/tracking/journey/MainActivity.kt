package com.tracking.journey

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btnStartStop: Button
    private lateinit var tvStatus: TextView
    private var tracking = false
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartStop = findViewById(R.id.btnStartStop)
        tvStatus = findViewById(R.id.tvStatus)

        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val allGranted = result.values.all { it }
            if (allGranted) {
                startTracking()
            } else {
                Toast.makeText(
                    this,
                    "We need Location and SMS permissions to start journey.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        btnStartStop.setOnClickListener {
            if (!tracking) {
                if (allPermissionsGranted()) {
                    // Permissions already granted
                    startTracking()
                } else {
                    // Ask for permissions
                    requestPermissionsLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.SEND_SMS
                    ))
                }
            } else {
                stopTracking()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }


    private fun startTracking() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        tracking = true
        tvStatus.text = "Journey Started"
        btnStartStop.text = "Stop Journey"
    }

    private fun stopTracking() {
        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)
        tracking = false
        tvStatus.text = "Journey Stopped"
        btnStartStop.text = "Start Journey"
    }
}