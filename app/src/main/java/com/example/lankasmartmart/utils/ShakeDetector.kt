package com.example.lankasmartmart.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Detects shake gestures using the device accelerometer.
 * When the user shakes the phone, it triggers the [onShake] callback.
 *
 * Usage:
 *   val shakeDetector = ShakeDetector(context) { /* on shake */ }
 *   shakeDetector.start()   // in onResume
 *   shakeDetector.stop()    // in onPause
 */
class ShakeDetector(
    private val context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Shake detection thresholds
    private val shakeThreshold = 12.0f      // Force needed to trigger (m/s²)
    private val shakeCooldownMs = 1500L      // Minimum time between shakes
    private var lastShakeTime = 0L

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Calculate acceleration magnitude (removing gravity ~9.8)
            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH

            if (acceleration > shakeThreshold) {
                val now = System.currentTimeMillis()
                if (now - lastShakeTime > shakeCooldownMs) {
                    lastShakeTime = now
                    onShake()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}
