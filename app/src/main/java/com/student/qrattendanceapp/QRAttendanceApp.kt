package com.student.qrattendanceapp



import android.app.Application
import com.student.qrattendanceapp.data.database.AppDatabase
import com.student.qrattendanceapp.data.repository.AppRepository

class QRAttendanceApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        AppRepository(
            database.userDao(),
            database.classRoomDao(),
            database.sessionDao(),
            database.attendanceDao()
        )
    }
}