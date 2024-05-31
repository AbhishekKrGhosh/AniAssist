package abhishek.aniassist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    var storedData: String? = null
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        storedData = sharedPreferences.getString("email", "")
        Handler().postDelayed({
            if (storedData == "" || storedData == null) {
                startActivity(Intent(this, Login::class.java))
                finish()
            } else {
                startActivity(Intent(this, GetLocation::class.java))
                finish()
            }
        }, 2000)
    }
}