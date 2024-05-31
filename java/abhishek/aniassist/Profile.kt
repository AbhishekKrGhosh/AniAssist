package abhishek.aniassist
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class Profile : AppCompatActivity() {
    lateinit var cityName: TextView
    lateinit var search: Button
    lateinit var playLogo: ImageButton
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mediaPlayer = MediaPlayer.create(this, R.raw.dogbarking)
        cityName = findViewById(R.id.city)
        search = findViewById(R.id.search)
        playLogo = findViewById(R.id.playLogo)
        playLogo.setOnClickListener{
            if (mediaPlayer.isPlaying) {
                mediaPlayer.seekTo(0)
            } else {
                mediaPlayer.start()
            }
        }



        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        val cityData = sharedPreferences.getString("city","")
        cityName.text = cityData
        search.setOnClickListener {
            val desiredCity = cityName.text.toString()
            var dcity = desiredCity
            dcity = dcity.replace(" ","")
            dcity = dcity.replace(".", "")
            dcity = dcity.replace("#", "")
            dcity = dcity.replace("\\$", "")
            dcity = dcity.replace("\\[", "")
            dcity = dcity.replace("]", "")

            if(dcity==""){
                Toast.makeText(this, "Please provide a valid city", Toast.LENGTH_LONG).show()
            }
            else {
                val sharedEdit = sharedPreferences.edit()
                sharedEdit.putString("city", dcity.lowercase())
                sharedEdit.putString("address", "")
                sharedEdit.apply()
                cityName.text = ""
                startActivity(Intent(this, Home::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onBackPressed() {
        super.onBackPressed()
            startActivity(Intent(this, Home::class.java))
    }
}