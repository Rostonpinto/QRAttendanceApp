package com.student.qrattendanceapp.ui.student

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.student.qrattendanceapp.QRAttendanceApp
import com.student.qrattendanceapp.R
import com.student.qrattendanceapp.data.entities.Attendance
import kotlinx.coroutines.*
import org.json.JSONObject

class StudentDashboardActivity : AppCompatActivity() {

    private var studentId = -1
    private lateinit var tvAttendance: TextView
    private lateinit var tvResult: TextView

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) processQR(result.contents)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        val prefs = getSharedPreferences("QRAttendancePrefs", MODE_PRIVATE)
        studentId = prefs.getInt("user_id", -1)
        val studentName = prefs.getString("user_name", "Student") ?: "Student"

        tvAttendance = findViewById(R.id.tvAttendanceSummary)
        tvResult = findViewById(R.id.tvScanResult)

        findViewById<TextView>(R.id.tvStudentName).text = "Welcome, $studentName!"

        findViewById<MaterialButton>(R.id.btnScan).setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Point camera at QR Code")
            options.setBeepEnabled(true)
            scanLauncher.launch(options)
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            getSharedPreferences("QRAttendancePrefs", MODE_PRIVATE).edit().clear().apply()
            finish()
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        observeAttendance()
    }

    private fun observeAttendance() {
        val repository = (application as QRAttendanceApp).repository
        repository.getAttendanceCountByStudent(studentId).observe(this) { count ->
            val total = 30
            val percentage = if (total > 0) (count * 100.0 / total) else 0.0
            val color = when {
                percentage >= 75 -> "#16A34A"
                percentage >= 50 -> "#F59E0B"
                else -> "#DC2626"
            }
            tvAttendance.text = "📊 Your Attendance\n" +
                    "Classes Attended: $count / $total\n" +
                    "Percentage: ${"%.1f".format(percentage)}%"
            tvAttendance.setTextColor(android.graphics.Color.parseColor(color))
        }
    }

    private fun processQR(qrContent: String) {
        val repository = (application as QRAttendanceApp).repository

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject(qrContent)
                val expiresAt = json.getLong("expires_at")
                val sessionId = json.optInt("session_id", -1)

                // Check expiry
                if (System.currentTimeMillis() > expiresAt) {
                    withContext(Dispatchers.Main) {
                        tvResult.text = "❌ QR Code has expired!"
                        tvResult.setTextColor(android.graphics.Color.RED)
                    }
                    return@launch
                }

                // ✅ Block if no sessionId — QR was generated before fix
                if (sessionId == -1) {
                    withContext(Dispatchers.Main) {
                        tvResult.text = "⚠️ Old QR detected!\nAsk teacher to generate a new QR."
                        tvResult.setTextColor(android.graphics.Color.parseColor("#F59E0B"))
                    }
                    return@launch
                }

                // Check duplicate
                val alreadyMarked = repository.checkAlreadyMarked(sessionId, studentId)
                if (alreadyMarked != null) {
                    withContext(Dispatchers.Main) {
                        tvResult.text = "⚠️ Attendance already marked for this class!"
                        tvResult.setTextColor(android.graphics.Color.parseColor("#F59E0B"))
                    }
                    return@launch
                }

                // Get QR details
                val faculty = json.optString("faculty_name", "Unknown")
                val subject = json.optString("subject", "Unknown")
                val session = json.optString("session", "Unknown")
                val date = json.optString("date", "Unknown")

                // ✅ Save with correct sessionId
                val attendance = Attendance(
                    sessionId = sessionId,
                    studentId = studentId,
                    classId = 0,
                    markedAt = System.currentTimeMillis()
                )
                repository.insertAttendance(attendance)

                withContext(Dispatchers.Main) {
                    tvResult.text = "✅ Attendance Marked!\n" +
                            "👨‍🏫 $faculty\n" +
                            "📚 $subject\n" +
                            "🕐 $session\n" +
                            "📅 $date"
                    tvResult.setTextColor(android.graphics.Color.parseColor("#16A34A"))
                    Toast.makeText(
                        this@StudentDashboardActivity,
                        "Attendance recorded!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvResult.text = "❌ Invalid QR Code"
                    tvResult.setTextColor(android.graphics.Color.RED)
                }
            }
        }
    }
}