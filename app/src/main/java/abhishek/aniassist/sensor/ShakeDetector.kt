package abhishek.aniassist.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastTime = 0L
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private var shakeListener: ShakeListener? = null

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() = sensorManager.unregisterListener(this)

    fun setShakeListener(listener: ShakeListener) { shakeListener = listener }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val now = System.currentTimeMillis()
        if (now - lastTime <= SHAKE_INTERVAL) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(
            (x - lastX) * (x - lastX) +
            (y - lastY) * (y - lastY) +
            (z - lastZ) * (z - lastZ)
        ).toDouble()

        if (acceleration > SHAKE_THRESHOLD) shakeListener?.onShakeDetected()

        lastX = x; lastY = y; lastZ = z; lastTime = now
    }

    interface ShakeListener {
        fun onShakeDetected()
    }

    companion object {
        private const val SHAKE_THRESHOLD = 15.0
        private const val SHAKE_INTERVAL = 500L
    }
}
