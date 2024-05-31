package abhishek.aniassist

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

class Found : AppCompatActivity(), ShakeDetector.ShakeListener {
    lateinit var progressDialog: ProgressDialog
    private lateinit var shakeDetector: ShakeDetector
    private  lateinit var aniRecyclerView: RecyclerView
    private  lateinit var tvLoadingData: ProgressBar
    private  lateinit var aniFoundList:  ArrayList<AnimalFoundInfo>
    private lateinit var db: DatabaseReference
    private lateinit var dbR: DatabaseReference
    lateinit var foundListBtn: Button
    lateinit var foundNewBtn: Button
    lateinit var foundListLinearLayout: LinearLayout
    lateinit var foundNewLinearLayout: LinearLayout
    lateinit var foundBtn: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var city: String
    lateinit var categoryet: EditText
    lateinit var choose_img: Button
    lateinit var pictureiv: ImageView
    lateinit var descriptionet: EditText
    lateinit var contactet: EditText
    lateinit var ca:String
    lateinit var d: String
    lateinit var co:String
    var picUri: String = ""
    lateinit var aniId: String
    lateinit var dateTime: String
    var fileUri: Uri? = null
    lateinit var foundtv : TextView
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found)
        categoryet = findViewById(R.id.categoryFound)
        choose_img = findViewById(R.id.picture)
        pictureiv = findViewById(R.id.pictureinfofound)
        descriptionet = findViewById(R.id.descriptionFound)
        contactet = findViewById(R.id.contactInfo)
        foundListLinearLayout = findViewById(R.id.foundListLinearLayout)
        foundNewLinearLayout = findViewById(R.id.foundNewLinearLayout)
        foundListBtn = findViewById(R.id.foundList)
        foundNewBtn = findViewById(R.id.foundNew)
        foundBtn = findViewById(R.id.postButton)
        foundtv = findViewById(R.id.foundtv)

        shakeDetector = ShakeDetector(this)
        shakeDetector.setShakeListener(this)


        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        val cityData = sharedPreferences.getString("city","")
        if (cityData != null) {
            city = cityData
        }

        db = FirebaseDatabase.getInstance().
        getReference("Location")

        choose_img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Image to Upload"), 0
            )
        }

        foundBtn.setOnClickListener {
            if (fileUri != null) {
                uploadImage()
            }
            else{
                uploadData()
            }
        }
        
        foundListLinearLayout.visibility = View.VISIBLE
        foundNewLinearLayout.visibility = View.GONE
        
        foundNewBtn.setBackgroundColor(Color.argb(50, 0, 100, 0))
        
        foundListBtn.setOnClickListener {
            foundListLinearLayout.visibility = View.VISIBLE
            foundNewLinearLayout.visibility = View.GONE

            foundListBtn.setBackgroundColor(Color.argb(255, 0, 100, 0))
            foundNewBtn.setBackgroundColor(Color.argb(50, 0, 100, 0))
            
            getFoundList()
        }
        foundNewBtn.setOnClickListener {
            aniId = db.push().key!!

            foundListLinearLayout.visibility = View.GONE
            foundNewLinearLayout.visibility = View.VISIBLE

            foundNewBtn.setBackgroundColor(Color.argb(255, 0, 100, 0))
            foundListBtn.setBackgroundColor(Color.argb(50, 0, 100, 0))

        }
        aniRecyclerView = findViewById(R.id.rvFound)
        aniRecyclerView.layoutManager = LinearLayoutManager(this)
        aniRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.progressBarFound)
        aniFoundList = arrayListOf<AnimalFoundInfo>()
        getFoundList()
        
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                pictureiv.setImageBitmap(bitmap)

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

        ca = categoryet.text.toString()
        d = descriptionet.text.toString()
        co = contactet.text.toString()

        val aniInfo = AnimalFoundInfo(ca, picUri, d, co, aniId, dateTime)

        db.child(city).child("Found").child(aniId).setValue(aniInfo)
            .addOnCompleteListener {
                categoryet.text.clear()
                descriptionet.text.clear()
                contactet.text.clear()
                Toast.makeText(this, "Found Info Uploaded", Toast.LENGTH_LONG).show()

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
        bitmap.compress(Bitmap.CompressFormat.JPEG, imgSize, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadToStorage(uri: Uri) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef: StorageReference = storageRef.child("Location").child(city).child("Found").child(aniId)

        imageRef.putFile(uri).addOnSuccessListener {
            progressDialog.dismiss()
            pictureiv.setImageResource(R.drawable.dimg)
            retrive_image()
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "File Upload Failed...", Toast.LENGTH_LONG).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun retrive_image() {
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("Location")
        val image_refrance: StorageReference = storageReference.child(city).child("Found").child(aniId)

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
    private fun getFoundList(){
        aniRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE
        foundtv.text = ""
        foundtv.visibility = View.GONE

        dbR = FirebaseDatabase.getInstance().
        getReference("Location")
        val dbRef : DatabaseReference = dbR.child(city).child("Found")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                aniFoundList.clear()
                if(snapshot.exists()){
                    for(aniSnap in snapshot.children.reversed()){
                        val aniData = aniSnap
                            .getValue(AnimalFoundInfo::class.java)
                        aniFoundList.add(aniData!!)
                    }
                    val mAdapter = AniFoundAdapter(aniFoundList)
                    aniRecyclerView.adapter = mAdapter
                    mAdapter.setOnItemClickListener(object :
                        AniFoundAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@Found,
                                ParticularFoundPage::class.java)
                            intent.putExtra("category", aniFoundList[position].category)
                            intent.putExtra("description", aniFoundList[position].description)
                            intent.putExtra("contact", aniFoundList[position].contact)
                            intent.putExtra("image", aniFoundList[position].uri)
                            intent.putExtra("aniId", aniFoundList[position].animalId)
                            intent.putExtra("currentCity",city)
                            startActivity(intent)
                        }
                    })
                    aniRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                    foundtv.text = ""
                    foundtv.visibility = View.GONE
                }else{
                    aniRecyclerView.visibility = View.GONE
                    tvLoadingData.visibility = View.GONE
                    foundtv.text = "\t\t\tNo Data Found for your city\n" +
                            "\t\t\tTry searching for other city\n" +
                            "Shake your phone to select another city"
                    foundtv.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.startListening()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stopListening()
    }

    override fun onShakeDetected() {
        startActivity(Intent(this, Profile::class.java))
    }
}