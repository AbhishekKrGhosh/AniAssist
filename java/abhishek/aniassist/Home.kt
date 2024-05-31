package abhishek.aniassist

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.log

class Home : AppCompatActivity(), ShakeDetector.ShakeListener  {
    private lateinit var shakeDetector: ShakeDetector
    private  lateinit var aniRecyclerView: RecyclerView
    private  lateinit var tvLoadingData: ProgressBar
    private  lateinit var aniList:  ArrayList<AnimalLostInfo>
    private lateinit var aniPostList: ArrayList<AnimalPostInfo>
    private lateinit var db: DatabaseReference
    lateinit var lost: ImageButton
    lateinit var logout: ImageButton
    lateinit var requestHelp: Button
    lateinit var missingBtn: Button
    lateinit var homeFoundBtn: ImageButton
    lateinit var homeTriviaButton: ImageButton
    lateinit var imageButtonLogo: ImageButton
    lateinit var requestsButton: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var em: String
    lateinit var city: String
    lateinit var hometv: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        shakeDetector = ShakeDetector(this)
        shakeDetector.setShakeListener(this)

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

        lost = findViewById(R.id.lostButton)
        logout = findViewById(R.id.logout)
        requestHelp = findViewById(R.id.buttonHelp)
        missingBtn = findViewById(R.id.buttonMissing)
        requestsButton = findViewById(R.id.buttonRequests)
        homeFoundBtn = findViewById(R.id.homeFoundBtn)
        homeTriviaButton = findViewById(R.id.homeTriviaButton)
        imageButtonLogo = findViewById(R.id.imageButtonlogo)
        hometv = findViewById(R.id.hometv)
        val b = AnimationUtils.loadAnimation(applicationContext, R.anim.sideslide)
        lost.startAnimation(b)
        val c = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        homeFoundBtn.startAnimation(c)
        homeTriviaButton.startAnimation(c)

        val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if(wifi.isWifiEnabled){
            imageButtonLogo.setImageResource(R.drawable.catwifi)
        }

        imageButtonLogo.setOnClickListener{
            startActivity(Intent(this, Profile::class.java))
        }
        homeTriviaButton.setOnClickListener{
            startActivity(Intent(this, WebView::class.java))
        }

        lost.setOnClickListener {
            startActivity(Intent(this, Lost::class.java))
        }
        logout.setOnClickListener {
            val sharedEdit = sharedPreferences.edit()
            sharedEdit.putString("email", "")
            sharedEdit.apply()
            startActivity(Intent(this, Login::class.java))
        }
        requestHelp.setOnClickListener {
            startActivity(Intent(this, Post::class.java))
        }
        homeFoundBtn.setOnClickListener {
            startActivity(Intent(this, Found::class.java))
        }

        aniRecyclerView = findViewById(R.id.rv)
        aniRecyclerView.layoutManager = LinearLayoutManager(this)
        aniRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        aniPostList = arrayListOf<AnimalPostInfo>()
        getPostAnimalData()
        missingBtn.setBackgroundColor(Color.argb(50, 0, 100, 0))

        missingBtn.setOnClickListener {
            aniList = arrayListOf<AnimalLostInfo>()
            getLostAnimalData()
            missingBtn.setBackgroundColor(Color.argb(255, 0, 100, 0))
            requestsButton.setBackgroundColor(Color.argb(50, 0, 100, 0))

        }
        requestsButton.setOnClickListener {

            aniPostList = arrayListOf<AnimalPostInfo>()
            getPostAnimalData()
            missingBtn.setBackgroundColor(Color.argb(50, 0, 100, 0))
            requestsButton.setBackgroundColor(Color.argb(255, 0, 100, 0))
        }

    }


    private fun getLostAnimalData(){
        aniRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE
        hometv.visibility = View.GONE
        hometv.text = ""

        db = FirebaseDatabase.getInstance().
        getReference("Location")
        val dbRef : DatabaseReference = db.child(city).child("Lost")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){

                aniList.clear()
                if(snapshot.exists()){
                    for(aniSnap in snapshot.children.reversed()){
                        val aniData = aniSnap
                            .getValue(AnimalLostInfo::class.java)
                        aniList.add(aniData!!)
                    }
                    val mAdapter = AniLostAdapter(aniList)
                    aniRecyclerView.adapter = mAdapter
                    mAdapter.setOnItemClickListener(object :
                        AniLostAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@Home,
                                ParticularLostPage::class.java)
                            intent.putExtra("category", aniList[position].category)
                            intent.putExtra("name", aniList[position].animName)
                            intent.putExtra("description", aniList[position].description)
                            intent.putExtra("location", aniList[position].lastSighted)
                            intent.putExtra("ownerName", aniList[position].ownerName)
                            intent.putExtra("ownerNumber", aniList[position].ownerNumber)
                            intent.putExtra("image", aniList[position].uri)
                            intent.putExtra("aniId", aniList[position].animalId)
                            intent.putExtra("currentCity",city)
                            startActivity(intent)
                        }
                    })
                    hometv.visibility = View.GONE
                    aniRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                    hometv.text = ""
                }
                else{
                    hometv.text = "\t\t\tNo Data Found for your city\n" +
                            "\t\t\tTry searching for other city\n" +
                            "Shake your phone to select another city"
                    hometv.visibility = View.VISIBLE
                    aniRecyclerView.visibility = View.GONE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getPostAnimalData(){
        aniRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE
        hometv.text = ""

        db = FirebaseDatabase.getInstance().
        getReference("Location")
        val dbRef : DatabaseReference = db.child(city).child("Post")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                aniPostList.clear()
                if(snapshot.exists()){
                    for(aniSnap in snapshot.children.reversed()){
                        val aniData = aniSnap
                            .getValue(AnimalPostInfo::class.java)
                        aniPostList.add(aniData!!)
                    }
                    val mAdapter = AniPostAdapter(aniPostList)
                    aniRecyclerView.adapter = mAdapter
                    mAdapter.setOnItemClickListener(object :
                        AniPostAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@Home,
                                ParticularPostPage::class.java)
                            intent.putExtra("problem", aniPostList[position].problem)
                            intent.putExtra("category", aniPostList[position].category)
                            intent.putExtra("condition", aniPostList[position].condition)
                            intent.putExtra("description", aniPostList[position].decription)
                            intent.putExtra("location", aniPostList[position].address)
                            intent.putExtra("image", aniPostList[position].uri)
                            intent.putExtra("aniId", aniPostList[position].animalId)
                            intent.putExtra("currentCity",city)

                            startActivity(intent)
                        }
                    })
                    aniRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                    hometv.text = ""
                }else{
                    hometv.text = "\t\t\tNo Data Found for your city\n" +
                            "\t\t\tTry searching for other city\n" +
                            "Shake your phone to select another city"
                    hometv.visibility = View.VISIBLE
                    aniRecyclerView.visibility = View.GONE
                    tvLoadingData.visibility = View.GONE
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


}