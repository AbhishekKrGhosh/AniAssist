package abhishek.aniassist

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ParticularFoundPage : AppCompatActivity() {
    lateinit var categorytv: TextView
    lateinit var contactInfotv: TextView
    lateinit var descriptiontv: TextView
    lateinit var imageFound: ImageView
    lateinit var aniId : String
    lateinit var curCity : String
    lateinit var btnFound: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_particular_found_page)
        categorytv = findViewById(R.id.categorytvFound)
        descriptiontv = findViewById(R.id.descriptionFoundtv)
        contactInfotv = findViewById(R.id.contactInfotvFound)
        imageFound = findViewById(R.id.FoundImg)
        btnFound = findViewById(R.id.foundButton)
        imageFound.setOnClickListener {
            openFullScreenImage()
        }
        btnFound.setOnClickListener {
            deleteRecord()
        }

        setValuesToViews()



    }
    private fun openFullScreenImage(){
        // Launch activity or dialog to display image in full screen
        val fullScreenIntent = Intent(this, FullScreenImageActivity::class.java)
        fullScreenIntent.putExtra("imageUri", intent.getStringExtra("image"))
        startActivity(fullScreenIntent)
    }

    private fun setValuesToViews(){
        categorytv.text = intent.getStringExtra("category")
        descriptiontv.text = intent.getStringExtra("description")
        contactInfotv.text = "Contact Info: " + intent.getStringExtra("contact")
        val imgUri = intent.getStringExtra("image")
        val animalId = intent.getStringExtra("aniId")
        if(animalId!=null){aniId = animalId}
        val currentCity = intent.getStringExtra("currentCity")
        if(currentCity!=null){curCity = currentCity}
        if(imgUri!="") {
            Picasso
                .get()
                .load(imgUri)
                .into(imageFound);
        }

    }
    private fun deleteRecord(){
        val intent = Intent(this@ParticularFoundPage,
            Verify::class.java)
        intent.putExtra("aniId", aniId)
        intent.putExtra("currentCity",curCity)
        intent.putExtra("Type", "Found")
        startActivity(intent)
    }

}