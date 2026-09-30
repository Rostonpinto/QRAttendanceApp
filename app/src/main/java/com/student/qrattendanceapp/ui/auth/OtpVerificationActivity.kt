package com.student.qrattendanceapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.student.qrattendanceapp.R

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        auth = FirebaseAuth.getInstance()

        val purpose = intent.getStringExtra("purpose") // "register" or "reset"
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val role = intent.getStringExtra("role") ?: ""

        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvTimer = findViewById<TextView>(R.id.tvTimer)
        val btnVerified = findViewById<MaterialButton>(R.id.btnVerified)
        val btnResend = findViewById<Button>(R.id.btnResend)
        val btnBack = findViewById<Button>(R.id.btnBack)

        tvEmail.text = "Verification email sent to:\n$email"

        // Send verification email
        sendVerificationEmail(email, password, purpose)

        // Start 60 second timer
        startTimer(tvTimer, btnResend)

        btnVerified.setOnClickListener {
            // Reload user to check verification status
            auth.currentUser?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Email verified!
                        val resultIntent = Intent()
                        resultIntent.putExtra("verified", true)
                        resultIntent.putExtra("purpose", purpose)
                        resultIntent.putExtra("email", email)
                        resultIntent.putExtra("password", password)
                        resultIntent.putExtra("name", name)
                        resultIntent.putExtra("role", role)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Email not verified yet! Please check your inbox.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        btnResend.setOnClickListener {
            sendVerificationEmail(email, password, purpose)
            startTimer(tvTimer, btnResend)
            Toast.makeText(this, "Verification email resent!", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            auth.signOut()
            finish()
        }
    }

    private fun sendVerificationEmail(email: String, password: String, purpose: String?) {
        if (purpose == "register" || purpose == "reset") {
            // Sign in or create temp account to send verification
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener {
                                Toast.makeText(
                                    this,
                                    "Verification email sent! Check your inbox.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        // User might already exist in Firebase, just sign in
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { signInTask ->
                                if (signInTask.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null && !user.isEmailVerified) {
                                        user.sendEmailVerification()
                                    }
                                }
                            }
                    }
                }
        }
    }

    private fun startTimer(tvTimer: TextView, btnResend: Button) {
        countDownTimer?.cancel()
        btnResend.isEnabled = false
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(ms: Long) {
                tvTimer.text = "Resend available in ${ms / 1000}s"
            }
            override fun onFinish() {
                tvTimer.text = "Didn't receive email?"
                btnResend.isEnabled = true
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}