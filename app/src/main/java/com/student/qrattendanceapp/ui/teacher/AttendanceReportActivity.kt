package com.student.qrattendanceapp.ui.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.qrattendanceapp.QRAttendanceApp
import com.student.qrattendanceapp.R
import com.student.qrattendanceapp.data.entities.Attendance
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AttendanceReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_report)

        val sessionId = intent.getIntExtra("session_id", -1)
        val sessionInfo = intent.getStringExtra("session_info") ?: ""

        val tvSessionInfo = findViewById<TextView>(R.id.tvSessionInfo)
        val tvCount = findViewById<TextView>(R.id.tvCount)
        val rvStudents = findViewById<RecyclerView>(R.id.rvStudents)
        val btnBack = findViewById<Button>(R.id.btnBack)

        tvSessionInfo.text = sessionInfo
        btnBack.setOnClickListener { finish() }

        rvStudents.layoutManager = LinearLayoutManager(this)

        val repository = (application as QRAttendanceApp).repository

        // Load attendance for this session
        repository.getAttendanceCountBySession(sessionId).observe(this) { count ->
            tvCount.text = "Total Students Present: $count"
        }

        // Load student list
        CoroutineScope(Dispatchers.IO).launch {
            val attendanceList = repository.getAttendanceBySession(sessionId)
            val studentNames = mutableListOf<String>()

            attendanceList.forEach { attendance ->
                val user = repository.getUserById(attendance.studentId)
                val time = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(Date(attendance.markedAt))
                studentNames.add("${user?.name ?: "Unknown"} — $time")
            }

            withContext(Dispatchers.Main) {
                rvStudents.adapter = StudentListAdapter(studentNames)
            }
        }
    }

    // Simple adapter for student list
    class StudentListAdapter(private val students: List<String>) :
        RecyclerView.Adapter<StudentListAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvStudentName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvName.text = "👤 ${students[position]}"
        }

        override fun getItemCount() = students.size
    }
}