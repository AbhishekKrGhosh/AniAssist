package abhishek.aniassist
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector(context: Context) : SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var lastTime: Long = 0
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private var shakeListener: ShakeListener? = null

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun setShakeListener(listener: ShakeListener) {
        shakeListener = listener
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - lastTime
            if (timeDifference > SHAKE_INTERVAL) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val deltaX = x - lastX
                val deltaY = y - lastY
                val deltaZ = z - lastZ
                val acceleration = Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble())
                if (acceleration > SHAKE_THRESHOLD) {
                    shakeListener?.onShakeDetected()
                }
                lastX = x
                lastY = y
                lastZ = z
                lastTime = currentTime
            }
        }
    }

    interface ShakeListener {
        fun onShakeDetected()
    }

    companion object {
        private const val SHAKE_THRESHOLD = 15
        private const val SHAKE_INTERVAL = 500
    }
}
