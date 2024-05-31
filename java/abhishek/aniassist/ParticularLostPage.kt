package abhishek.aniassist

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ParticularLostPage : AppCompatActivity() {
    lateinit var nametv: TextView
    lateinit var categorytv: TextView
    lateinit var lastSightedtv: TextView
    lateinit var descriptiontv: TextView
    lateinit var ownerNametv: TextView
    lateinit var ownerNumbertv: TextView
    lateinit var imageLost: ImageView
    lateinit var aniId : String
    lateinit var curCity : String
    lateinit var btnFound: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_particular_lost_page)
        categorytv = findViewById(R.id.categorytv)
        descriptiontv = findViewById(R.id.descriptionLosttv)
        nametv = findViewById(R.id.nametv)
        lastSightedtv = findViewById(R.id.lastSightedtv)
        ownerNametv = findViewById(R.id.ownerNametv)
        ownerNumbertv = findViewById(R.id.ownerNumbertv)
        imageLost = findViewById(R.id.lostImg)
        btnFound = findViewById(R.id.found)
        setValuesToViews()
        btnFound.setOnClickListener {
            deleteRecord()
        }
        imageLost.setOnClickListener {
            openFullScreenImage()
        }


    }private fun openFullScreenImage(){
        // Launch activity or dialog to display image in full screen
        val fullScreenIntent = Intent(this, FullScreenImageActivity::class.java)
        fullScreenIntent.putExtra("imageUri", intent.getStringExtra("image"))
        startActivity(fullScreenIntent)
    }

    private fun setValuesToViews(){
        categorytv.text = intent.getStringExtra("category")
        nametv.text = intent.getStringExtra("name")
        descriptiontv.text = intent.getStringExtra("description")
        lastSightedtv.text ="Last Sighted at: " + intent.getStringExtra("location")
        ownerNametv.text = "Owner Name: " + intent.getStringExtra("ownerName")
        ownerNumbertv.text = "Owner Number: " + intent.getStringExtra("ownerNumber")
        val imgUri = intent.getStringExtra("image")
        val animalId = intent.getStringExtra("aniId")
        if(animalId!=null){aniId = animalId}
        val currentCity = intent.getStringExtra("currentCity")
        if(currentCity!=null){curCity = currentCity}
        if(imgUri!="") {
            Picasso
                .get()
                .load(imgUri)
                .into(imageLost);
        }

    }
    private fun deleteRecord(){
        val intent = Intent(this@ParticularLostPage,
            Verify::class.java)
        intent.putExtra("aniId", aniId)
        intent.putExtra("currentCity",curCity)
        intent.putExtra("Type", "Lost")
        startActivity(intent)
    }

}