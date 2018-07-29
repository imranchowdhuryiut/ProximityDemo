package com.example.imranchowdhury.proximitydemo


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mySensorManager: SensorManager? = null
    private var myProximitySensor: Sensor? = null
    private var mPowerManager: PowerManager? = null
    private var mWindowManager: WindowManager? = null
    private var mWakeLock: WakeLock? = null

    private val mProximityChangeListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        }

        override fun onSensorChanged(p0: SensorEvent?) {
            val distance = p0!!.values!![0]
            if (distance < myProximitySensor!!.maximumRange) {
                tvNearOrFar.text = "Near"
                mWakeLock = mPowerManager?.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag")
                mWakeLock?.acquire()

            } else {
                tvNearOrFar.text = "Far"
                mWakeLock = mPowerManager?.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag")
                mWakeLock?.acquire()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        myProximitySensor = mySensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        mPowerManager = getSystemService(POWER_SERVICE) as PowerManager?

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        mWindowManager!!.defaultDisplay


        if (myProximitySensor == null) {
            tvNearOrFar.text = "Proximity sensor is not available"
            return
        }
        tvNearOrFar.text = "Proximity sensor is available"
        mWakeLock?.acquire()
    }

    override fun onPostResume() {
        super.onPostResume()
        mySensorManager!!.registerListener(mProximityChangeListener, myProximitySensor, 20000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mySensorManager!!.unregisterListener(mProximityChangeListener)
    }
}
