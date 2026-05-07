package com.student.qrattendanceapp.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classId: Int,
    val qrData: String,
    val generatedAt: Long,
    val expiresAt: Long,
    val isActive: Boolean = true
)