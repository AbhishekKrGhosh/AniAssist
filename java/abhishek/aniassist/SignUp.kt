package abhishek.aniassist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    lateinit var login: TextView
    lateinit var a: FirebaseAuth
    lateinit var db: DatabaseReference
    lateinit var name: EditText
    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var signup: Button
    lateinit var n: String
    lateinit var e: String
    lateinit var em: String
    lateinit var p: String
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        login = findViewById(R.id.logintvsign)
        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        login.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }
        a = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().
        getReference("Users")
        name = findViewById(R.id.namesign)
        email = findViewById(R.id.emailsign)
        pass = findViewById(R.id.passwordsign)
        signup = findViewById(R.id.signup)
        signup.setOnClickListener {
            n = name.text.toString()
            e = email.text.toString()
            p = pass.text.toString()
            em = e
            em = em.replace(".", "")
            em = em.replace("#", "")
            em = em.replace("\\$", "")
            em = em.replace("\\[", "")
            em = em.replace("]", "")
            if (n.isEmpty()) {
                name.error = "Please enter name"
            }
            if (e.isEmpty()) {
                email.error = "Please enter email"
            }
            if (p.isEmpty()) {
                pass.error = "Please enter password"
            }
            if (p.length<6) {
                pass.error = "Password should be of at-least 6 characters"
            }
            db.child(em.lowercase()).child("name").setValue(n)
            if(e!="" && e!=null && p!="" && p!=null) {
                a.createUserWithEmailAndPassword(e, p)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val sharedEdit = sharedPreferences.edit()
                            sharedEdit.putString("email", e.lowercase())
                            sharedEdit.apply()
                            startActivity(Intent(this, GetLocation::class.java))
                        } else {
                            Toast.makeText(
                                this,
                                "SignUp not done, " + it.exception,
                                Toast.LENGTH_LONG
                            )
                                .show()

                        }
                    }
            }
        }

    }
}