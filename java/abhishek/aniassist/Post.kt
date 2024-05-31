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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

class Post : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    lateinit var sharedPreferences: SharedPreferences
    lateinit var em: String
    lateinit var db: DatabaseReference
    lateinit var problem: EditText
    lateinit var category: EditText
    lateinit var condition: EditText
    lateinit var description: EditText
    lateinit var locationET: TextView
    lateinit var choose_img: Button
    lateinit var image_view: ImageView
    lateinit var aniPostButton: Button
    var fileUri: Uri? = null
    lateinit var p:String
    lateinit var c:String
    lateinit var co:String
    lateinit var d:String
    lateinit var l:String
    lateinit var aniId: String
    lateinit var dateTime: String
    var picUri: String = ""
    lateinit var city : String
    lateinit var address: String
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        val storedData = sharedPreferences.getString("email","")
        val cityData = sharedPreferences.getString("city","")
        val addressData = sharedPreferences.getString("address","")
        if (addressData != null) {
            address = addressData
        }
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

        problem = findViewById(R.id.problem)
        category = findViewById(R.id.animaltype)
        condition = findViewById(R.id.condition)
        description = findViewById(R.id.description)
        locationET = findViewById(R.id.locationet)
        choose_img = findViewById(R.id.picture)
        image_view = findViewById(R.id.pictureinfopost)
        aniPostButton = findViewById(R.id.postButton)

        locationET.text = address

        choose_img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Image to Upload"), 0
            )
        }

        aniId = db.push().key!!
        aniPostButton.setOnClickListener {
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

        p = problem.text.toString()
        c = category.text.toString()
        co = condition.text.toString()
        d = description.text.toString()
        l = locationET.text.toString()

        val aniInfo = AnimalPostInfo(p, c, co, d, l, aniId, dateTime, picUri)

        db.child(city).child("Post").child(aniId).setValue(aniInfo)
            .addOnCompleteListener {
                problem.text.clear()
                category.text.clear()
                condition.text.clear()
                description.text.clear()
                Toast.makeText(this, "Request for Help Uploaded", Toast.LENGTH_LONG).show()


            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadImage() {
        if (fileUri != null) {
            progressDialog = ProgressDialog(this)
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, imgSize, outputStream) // Adjust quality as needed (0-100)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadToStorage(uri: Uri) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef: StorageReference = storageRef.child("Location").child(city).child("Post").child(aniId)

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
        val image_refrance: StorageReference = storageReference.child(city).child("Post").child(aniId)

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
                Toast.makeText(this,"Image Retrived Failed: " + exception.message,Toast.LENGTH_LONG).show()

            }
    }
}