package abhishek.aniassist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val btnResetPassword = findViewById<Button>(R.id.btnResetPassword)
        val cancel = findViewById<Button>(R.id.btnCancelPassword)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        auth = FirebaseAuth.getInstance()

        cancel.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        btnResetPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendPasswordResetEmail(email)
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset email sent to $email",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send password reset email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}