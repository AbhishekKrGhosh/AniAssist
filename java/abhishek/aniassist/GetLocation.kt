package abhishek.aniassist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class GetLocation : AppCompatActivity() {
    lateinit var city: String
    var address: String = ""
    private val permissionId = 2
    lateinit var cityName: TextView
    lateinit var setCity: Button
    lateinit var getCity: Button
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_location)
        cityName = findViewById(R.id.city)
        setCity = findViewById(R.id.setCity)
        getCity = findViewById(R.id.getCity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCity.setOnClickListener {
            getLocation()
        }



        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)

        setCity.setOnClickListener {
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
                if(address!=""){
                    sharedEdit.putString("address", address)
                }
                else{
                    sharedEdit.putString("address", "")
                }
                sharedEdit.apply()
                cityName.text = ""
                startActivity(Intent(this, Home::class.java))
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        city = list[0].locality
                        address = list[0].getAddressLine(0)
                        var dcity = city
                        dcity = dcity.replace(" ","")
                        dcity = dcity.replace(".", "")
                        dcity = dcity.replace("#", "")
                        dcity = dcity.replace("\\$", "")
                        dcity = dcity.replace("\\[", "")
                        dcity = dcity.replace("]", "")
                        val sharedEdit = sharedPreferences.edit()
                        sharedEdit.putString("city", dcity.lowercase())
                        sharedEdit.putString("address", address)
                        sharedEdit.apply()
                        cityName.text = dcity.lowercase()
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }


    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == permissionId){
            if(grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocation()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}