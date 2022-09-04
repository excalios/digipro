package com.example.digipro

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import android.widget.Toast


class HeartBeatService: Service(), SensorEventListener {
    private var TAG: String = "HeartBeatService"

    private var mSensorManager: SensorManager? = null
    private var mHeartBeat: Sensor? = null

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        Log.d("HeartBeatService", "Service Created")
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            mHeartBeat = mSensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_BEAT)
            mSensorManager!!.registerListener(this@HeartBeatService, mHeartBeat, SensorManager.SENSOR_DELAY_NORMAL)

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        mSensorManager!!.unregisterListener(this@HeartBeatService)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d(TAG, "onSensorChanged: Sensor")
        if (event != null) {
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                Toast.makeText(this, "Heart Rate Detected", Toast.LENGTH_SHORT).show()
                val msg = "Heart Rate: " + event.values.get(0) as Int
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            } else if (event.sensor.getType() == Sensor.TYPE_HEART_BEAT) {
                val msg = "Heart Beat: " + event.values.get(0) as Int
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            } else Log.d(TAG, "Unknown sensor type")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged - accuracy: $accuracy")
    }
}