package com.student.qrattendanceapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.student.qrattendanceapp.ui.auth.ForgotPasswordActivity
import com.student.qrattendanceapp.ui.auth.RegisterActivity
import com.student.qrattendanceapp.ui.teacher.TeacherDashboardActivity
import com.student.qrattendanceapp.ui.student.StudentDashboardActivity
import com.student.qrattendanceapp.viewmodel.AuthViewModel
import com.student.qrattendanceapp.viewmodel.AuthViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("QRAttendancePrefs", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)
        val userRole = prefs.getString("user_role", "")
        if (userId != -1 && !userRole.isNullOrEmpty()) { navigateToDashboard(userRole); return }

        setContentView(R.layout.activity_main)
        val repository = (application as QRAttendanceApp).repository
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            authViewModel.login(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )
        }
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        authViewModel.loginResult.observe(this) { user ->
            if (user != null) {
                prefs.edit()
                    .putInt("user_id", user.id)
                    .putString("user_role", user.role)
                    .putString("user_name", user.name)
                    .apply()
                navigateToDashboard(user.role)
            }
        }
        authViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDashboard(role: String) {
        val intent = if (role == "teacher")
            Intent(this, TeacherDashboardActivity::class.java)
        else
            Intent(this, StudentDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}