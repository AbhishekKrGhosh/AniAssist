package abhishek.aniassist

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Lost : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    lateinit var sharedPreferences: SharedPreferences
    lateinit var em: String
    lateinit var city: String
    lateinit var db: DatabaseReference
    lateinit var aniCategory: EditText
    lateinit var aniName: EditText
    lateinit var ownerName: EditText
    lateinit var ownerNumber: EditText
    lateinit var aniDescription: EditText
    lateinit var aniLastSighted: EditText
    lateinit var choose_img: Button
    lateinit var image_view: ImageView
    lateinit var aniLostButton: Button
    lateinit var ac:String
    lateinit var an:String
    lateinit var on:String
    lateinit var onu:String
    lateinit var ad:String
    lateinit var al:String
    var fileUri: Uri? = null
    var picUri: String = ""
    var asl : Boolean = true
    lateinit var aniId: String
    lateinit var dateTime: String
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost)
        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        val storedData = sharedPreferences.getString("email","")
        val cityData = sharedPreferences.getString("city","")
        if (cityData != null) {
            city = cityData
        }
        if (storedData != null) {
            em = storedData
        }

        em = em.replace(".", "")
        em = em.replace("#", "")
        em = em.replace("\\$", "")
        em = em.replace("\\[", "")
        em = em.replace("]", "")

        db = FirebaseDatabase.getInstance().
        getReference("Location")

        aniCategory = findViewById(R.id.categorylost)
        aniName = findViewById(R.id.namelost)
        ownerName = findViewById(R.id.ownerlost)
        ownerNumber = findViewById(R.id.ownernumberlost)
        aniDescription = findViewById(R.id.descriptionlost)
        aniLastSighted = findViewById(R.id.lastlocationlost)
        choose_img = findViewById(R.id.picturelost)
        image_view = findViewById(R.id.pictureinfolost)
        aniLostButton = findViewById(R.id.buttonLost)

        choose_img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Image to Upload"), 0
            )
        }

        aniId = db.push().key!!

        aniLostButton.setOnClickListener {
            if (fileUri != null) {
                uploadImage()
            }
            else{
                uploadData()
            }
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                image_view.setImageBitmap(bitmap)

            } catch (e: Exception) {
                Log.e("Exception", "Error: " + e)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadData(){
        var currentDateTime = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        dateTime = currentDateTime.format(formatter)

        ac = aniCategory.text.toString()
        an = aniName.text.toString()
        on = ownerName.text.toString()
        onu = ownerNumber.text.toString()
        ad = aniDescription.text.toString()
        al = aniLastSighted.text.toString()

        val aniInfo = AnimalLostInfo(ac, an, on, onu, ad, al, aniId, dateTime, picUri, asl)

        db.child(city).child("Lost").child(aniId).setValue(aniInfo)
            .addOnCompleteListener {
                aniCategory.text.clear()
                aniName.text.clear()
                ownerName.text.clear()
                ownerNumber.text.clear()
                aniDescription.text.clear()
                aniLastSighted.text.clear()
                Toast.makeText(this, "Missing Report Uploaded", Toast.LENGTH_LONG).show()

            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadImage() {
        if (fileUri != null) {
            progressDialog = ProgressDialog(this) // Initialize progressDialog here
            progressDialog.setTitle("Uploading Image...")
            progressDialog.setMessage("Processing...")
            progressDialog.show()

            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)

                val fileSizeBytes = getFileSize(fileUri!!)

                if (fileSizeBytes > 5000 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 5)
                    uploadToStorage(compressedFileUri)
                }
                else if (fileSizeBytes > 2000 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 15)
                    uploadToStorage(compressedFileUri)
                }
                else if (fileSizeBytes > 800 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 20)
                    uploadToStorage(compressedFileUri)
                }
                else if (fileSizeBytes > 300 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 30)
                    uploadToStorage(compressedFileUri)
                }
                else {
                    uploadToStorage(fileUri!!)
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun getFileSize(uri: Uri): Long {
        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileSize = fileDescriptor?.statSize ?: 0
        fileDescriptor?.close()
        return fileSize
    }

    private fun compressImage(bitmap: Bitmap, imgSize: Int): Uri {
        val file = File.createTempFile("compressed_image", ".jpg", applicationContext.cacheDir)
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, imgSize, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file)
    }

@RequiresApi(Build.VERSION_CODES.O)
private fun uploadToStorage(uri: Uri) {
    val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    val imageRef: StorageReference = storageRef.child("Location").child(city).child("Lost").child(aniId)

    imageRef.putFile(uri).addOnSuccessListener {
        progressDialog.dismiss()
        image_view.setImageResource(R.drawable.dimg)
        retrive_image()
    }.addOnFailureListener {
        progressDialog.dismiss()
        Toast.makeText(applicationContext, "File Upload Failed...", Toast.LENGTH_LONG).show()
    }
}

    @RequiresApi(Build.VERSION_CODES.O)
    fun retrive_image() {
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("Location")
        val image_refrance: StorageReference = storageReference.child(city).child("Lost").child(aniId)

        val progressDialog2 = ProgressDialog(this)
        progressDialog2.setTitle("Please Wait...")
        progressDialog2.setMessage("Processing...")
        progressDialog2.show()

        image_refrance.downloadUrl.addOnSuccessListener { uri: Uri ->
            picUri = uri.toString()
            uploadData()

            progressDialog2.dismiss()
            Toast.makeText(this,"Data Uploaded Successfully",Toast.LENGTH_LONG).show()
        }
            .addOnFailureListener { exception ->
                progressDialog2.dismiss()
                Toast.makeText(this,"Image Retrived Failed: "+exception.message,Toast.LENGTH_LONG).show()

            }
    }
}