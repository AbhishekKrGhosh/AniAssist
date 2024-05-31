package abhishek.aniassist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ParticularPostPage : AppCompatActivity() {
    lateinit var problemtv: TextView
    lateinit var categorytv: TextView
    lateinit var conditiontv: TextView
    lateinit var descriptiontv: TextView
    lateinit var locationtv: TextView
    lateinit var injured: ImageView
    lateinit var aniId : String
    lateinit var curCity : String
    lateinit var btnDelete: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_particular_post_page)
        problemtv = findViewById(R.id.problemtv)
        categorytv = findViewById(R.id.categorytv)
        conditiontv = findViewById(R.id.conditiontv)
        descriptiontv = findViewById(R.id.descriptiontv)
        locationtv = findViewById(R.id.locationtv)
        injured = findViewById(R.id.injured)
        btnDelete = findViewById(R.id.done)
        setValuesToViews()
        btnDelete.setOnClickListener {
            deleteRecord()
        }
        injured.setOnClickListener {
            openFullScreenImage()
        }


    }
    private fun openFullScreenImage(){
        // Launch activity or dialog to display image in full screen
        val fullScreenIntent = Intent(this, FullScreenImageActivity::class.java)
        fullScreenIntent.putExtra("imageUri", intent.getStringExtra("image"))
        startActivity(fullScreenIntent)
    }

    private fun setValuesToViews(){
        problemtv.text = intent.getStringExtra("problem")
        categorytv.text = intent.getStringExtra("category")
        conditiontv.text = "Condition: " + intent.getStringExtra("condition")
        descriptiontv.text = intent.getStringExtra("description")
        locationtv.text = "Location: " + intent.getStringExtra("location")
        val imgUri = intent.getStringExtra("image")
        val animalId = intent.getStringExtra("aniId")
        if(animalId!=null){aniId = animalId}
        val currentCity = intent.getStringExtra("currentCity")
        if(currentCity!=null){curCity = currentCity}
        if(imgUri!="") {
            Picasso
                .get()
                .load(imgUri)
                .into(injured);
        }

    }
    private fun deleteRecord(){
        val intent = Intent(this@ParticularPostPage,
            Verify::class.java)
        intent.putExtra("aniId", aniId)
        intent.putExtra("currentCity",curCity)
        intent.putExtra("Type", "Post")
        startActivity(intent)
    }

}