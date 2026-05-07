package com.student.qrattendanceapp.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val studentId: Int,
    val classId: Int,
    val markedAt: Long,
    val status: String = "present"
)