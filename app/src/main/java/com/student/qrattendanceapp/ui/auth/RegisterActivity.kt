package com.student.qrattendanceapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.student.qrattendanceapp.QRAttendanceApp
import com.student.qrattendanceapp.R
import com.student.qrattendanceapp.viewmodel.AuthViewModel
import com.student.qrattendanceapp.viewmodel.AuthViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    private val otpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val name = data?.getStringExtra("name") ?: ""
            val email = data?.getStringExtra("email") ?: ""
            val password = data?.getStringExtra("password") ?: ""
            val role = data?.getStringExtra("role") ?: ""
            // Email verified → save to our DB
            authViewModel.register(name, email, password, role)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val repository = (application as QRAttendanceApp).repository
        authViewModel = ViewModelProvider(
            this, AuthViewModelFactory(repository)
        )[AuthViewModel::class.java]

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val rgRole = findViewById<RadioGroup>(R.id.rgRole)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val role = if (rgRole.checkedRadioButtonId == R.id.rbTeacher)
                "teacher" else "student"

            // Validate before sending OTP
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!email.endsWith(".edu") && !email.contains(".edu.")) {
                Toast.makeText(this, "Only .edu emails allowed!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password must be 6+ characters!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Go to email verification screen
            val intent = Intent(this, OtpVerificationActivity::class.java).apply {
                putExtra("purpose", "register")
                putExtra("email", email)
                putExtra("password", password)
                putExtra("name", name)
                putExtra("role", role)
            }
            otpLauncher.launch(intent)
        }

        authViewModel.registerResult.observe(this) {
            Toast.makeText(this, "✅ Registered Successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }

        authViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}