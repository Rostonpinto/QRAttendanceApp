package com.student.qrattendanceapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.student.qrattendanceapp.data.entities.Attendance

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    // For Teacher — count by sessionId
    @Query("SELECT COUNT(*) FROM attendance WHERE sessionId = :sessionId")
    fun getAttendanceCountBySession(sessionId: Int): LiveData<Int>

    // For Student — count by studentId
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId")
    fun getAttendanceCountByStudent(studentId: Int): LiveData<Int>

    // Check duplicate scan
    @Query("SELECT * FROM attendance WHERE sessionId = :sessionId AND studentId = :studentId LIMIT 1")
    suspend fun checkAlreadyMarked(sessionId: Int, studentId: Int): Attendance?

    // Get all attendance for a student
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY markedAt DESC")
    fun getAttendanceByStudent(studentId: Int): LiveData<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE sessionId = :sessionId ORDER BY markedAt ASC")
    suspend fun getAttendanceBySession(sessionId: Int): List<Attendance>
}