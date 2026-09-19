package com.student.qrattendanceapp.ui.auth

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.student.qrattendanceapp.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val btnReset = findViewById<MaterialButton>(R.id.btnReset)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()

            // Empty check
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // .edu check
            if (!email.endsWith(".edu") && !email.contains(".edu.")) {
                Toast.makeText(this, "Only .edu emails allowed!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Send Firebase reset email
            btnReset.isEnabled = false
            btnReset.text = "Sending..."

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    btnReset.isEnabled = true
                    btnReset.text = "Send Reset Link"

                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "✅ Reset link sent to $email!\nCheck your inbox and click the link to reset password.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        val errorMsg = when {
                            task.exception?.message?.contains("no user record") == true ->
                                "❌ No account found with this email!"
                            task.exception?.message?.contains("badly formatted") == true ->
                                "❌ Invalid email format!"
                            else -> "❌ Failed to send reset email. Try again!"
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}