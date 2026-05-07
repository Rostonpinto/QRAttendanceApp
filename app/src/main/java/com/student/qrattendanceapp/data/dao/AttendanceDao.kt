
package com.student.qrattendanceapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.student.qrattendanceapp.data.entities.Attendance

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Query("SELECT COUNT(*) FROM attendance WHERE sessionId = :sessionId")
    fun getAttendanceCount(sessionId: Int): LiveData<Int>

    @Query("SELECT * FROM attendance WHERE sessionId = :sessionId AND studentId = :studentId LIMIT 1")
    suspend fun checkAlreadyMarked(sessionId: Int, studentId: Int): Attendance?
}