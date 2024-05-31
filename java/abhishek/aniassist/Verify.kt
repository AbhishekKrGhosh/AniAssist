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

class Verify : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    lateinit var sharedPreferences: SharedPreferences
    lateinit var em: String
    lateinit var proofsubmit: Button
    lateinit var choose_img:Button
    lateinit var image_view: ImageView
    lateinit var descet: EditText
    lateinit var perCity: String
    lateinit var db: DatabaseReference
    lateinit var d: String
    var fileUri: Uri? = null
    var picUri: String = ""
    lateinit var aniId: String
    lateinit var perId:String
    lateinit var perType: String
    lateinit var dateTime: String
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        proofsubmit = findViewById(R.id.proofsubmit)
        choose_img = findViewById(R.id.imageproof)
        image_view = findViewById(R.id.proofImage)
        descet = findViewById(R.id.descriptionet)

        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        val storedData = sharedPreferences.getString("email","")

        if (storedData != null) {
            em = storedData
        }

        em = em.replace(".", "")
        em = em.replace("#", "")
        em = em.replace("\\$", "")
        em = em.replace("\\[", "")
        em = em.replace("]", "")

        val incity = intent.getStringExtra("currentCity")
        val inaniId = intent.getStringExtra("aniId")
        val intype = intent.getStringExtra("Type")
        if(incity!=null){
            perCity = incity
        }
        if(inaniId!=null){
            perId = inaniId
        }
        if(intype!=null){
            perType = intype
        }


        db = FirebaseDatabase.getInstance().
        getReference("Users")

        aniId = db.push().key!!

        choose_img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Image to Upload"), 0
            )
        }

        proofsubmit.setOnClickListener {
            if (fileUri != null) {
                uploadImage()
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

        d = descet.text.toString()


        val aniInfo = VerifyInfo(aniId, d, picUri, dateTime)

        db.child(em).child("Proof").child(perType).child(aniId).setValue(aniInfo)
            .addOnCompleteListener {
                descet.text.clear()
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
                if (fileSizeBytes > 10000 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 0)
                    uploadToStorage(compressedFileUri)
                }
                else if (fileSizeBytes > 5000 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 1)
                    uploadToStorage(compressedFileUri)

                }
                else if (fileSizeBytes > 2000 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 2)
                    uploadToStorage(compressedFileUri)
                }
                else if (fileSizeBytes > 800 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 5)
                    uploadToStorage(compressedFileUri)

                }
                else if (fileSizeBytes > 300 * 1024) {
                    val compressedFileUri = compressImage(bitmap, 10)
                    uploadToStorage(compressedFileUri)
                }
                else if (fileSizeBytes > 100 * 1024) {
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
        val imageRef: StorageReference = storageRef.child("Users").child(em).child("Proof").child(perType).child(aniId)

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
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("Users")
        val image_refrance: StorageReference = storageReference.child(em).child("Proof").child(perType).child(aniId)

        val progressDialog2 = ProgressDialog(this)
        progressDialog2.setTitle("Please Wait...")
        progressDialog2.setMessage("Processing...")
        progressDialog2.show()

        image_refrance.downloadUrl.addOnSuccessListener { uri: Uri ->
            picUri = uri.toString()
            uploadData()

            progressDialog2.dismiss()
            Toast.makeText(this,"Data Uploaded Successfully", Toast.LENGTH_LONG).show()
            deleteInfo()
        }
            .addOnFailureListener { exception ->
                progressDialog2.dismiss()
                Toast.makeText(this,"Image Retrived Failed: "+exception.message, Toast.LENGTH_LONG).show()

            }
    }
    fun deleteInfo(){
        val storage: StorageReference = FirebaseStorage.getInstance().getReference("Location")
        val storageRef: StorageReference = storage.child(perCity).child(perType).child(perId)
        storageRef.delete().addOnSuccessListener {
            val dbRef = FirebaseDatabase.getInstance()
                .getReference("Location").child(perCity).child(perType).child(perId)
            val mTask = dbRef.removeValue()
            mTask.addOnSuccessListener {
                Toast.makeText(this, "Thank you for your service", Toast.LENGTH_LONG).show()
                val intent = Intent(this, Home::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener{ error ->
                Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            val dbRef = FirebaseDatabase.getInstance()
                .getReference("Location").child(perCity).child(perType).child(perId)
            val mTask = dbRef.removeValue()
            mTask.addOnSuccessListener {
                Toast.makeText(this, "Thank you for your service", Toast.LENGTH_LONG).show()
                val intent = Intent(this, Home::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener{ error ->
                Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
            }
        }


    }
}