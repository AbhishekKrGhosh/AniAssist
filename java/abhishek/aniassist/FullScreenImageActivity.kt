package abhishek.aniassist

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class FullScreenImageActivity : AppCompatActivity() {
    lateinit var fullScreenImageView : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        fullScreenImageView = findViewById(R.id.fullScreenImageView)

        val imageUri = intent.getStringExtra("imageUri")

        if(imageUri!="") {
            Picasso
                .get()
                .load(imageUri)
                .into(fullScreenImageView);
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
