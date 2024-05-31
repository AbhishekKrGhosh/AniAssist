package abhishek.aniassist
import android.annotation.SuppressLint
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

class Login : AppCompatActivity() {
    lateinit var signup : TextView
    lateinit var a: FirebaseAuth
    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var login: Button
    lateinit var forgot: TextView
    lateinit var e: String
    lateinit var p: String
    lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signup = findViewById(R.id.signtvlogin)
        forgot = findViewById(R.id.forgot)
        sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE)
        signup.setOnClickListener{
            startActivity(Intent(this, SignUp::class.java))
        }
        forgot.setOnClickListener{
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        a = FirebaseAuth.getInstance()
        email = findViewById(R.id.emaillogin)
        pass = findViewById(R.id.passwordlogin)
        login = findViewById(R.id.login)
        login.setOnClickListener {
            e = email.text.toString()
            p = pass.text.toString()
            if(e!="" && e!=null && p!="" && p!=null) {
                a.signInWithEmailAndPassword(e, p)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val sharedEdit = sharedPreferences.edit()
                            sharedEdit.putString("email", e.lowercase())
                            sharedEdit.apply()
                            startActivity(Intent(this, GetLocation::class.java))
                        } else {
                            Toast.makeText(
                                this,
                                "error while login, please check email or password",
                                Toast.LENGTH_LONG
                            )
                                .show()

                        }
                    }
            }
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}