package com.student.qrattendanceapp.data.repository

import com.student.qrattendanceapp.data.dao.*
import com.student.qrattendanceapp.data.entities.*

class AppRepository(
    private val userDao: UserDao,
    private val classRoomDao: ClassRoomDao,
    private val sessionDao: SessionDao,
    private val attendanceDao: AttendanceDao
) {
    suspend fun registerUser(user: User) = userDao.insertUser(user)
    suspend fun login(email: String, password: String) = userDao.login(email, password)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun insertClass(c: ClassRoom) = classRoomDao.insertClass(c)
    suspend fun deleteClass(c: ClassRoom) = classRoomDao.deleteClass(c)
    fun getClassesByTeacher(id: Int) = classRoomDao.getClassesByTeacher(id)
    suspend fun getClassByCode(code: String) = classRoomDao.getClassByCode(code)

    suspend fun insertSession(s: Session) = sessionDao.insertSession(s)
    suspend fun updateSession(s: Session) = sessionDao.updateSession(s)
    suspend fun getSessionById(id: Int) = sessionDao.getSessionById(id)
    suspend fun endSession(id: Int) = sessionDao.endSession(id)

    suspend fun insertAttendance(a: Attendance) = attendanceDao.insertAttendance(a)

    // Teacher — count by session
    fun getAttendanceCountBySession(sessionId: Int) =
        attendanceDao.getAttendanceCountBySession(sessionId)

    // Student — count by student
    fun getAttendanceCountByStudent(studentId: Int) =
        attendanceDao.getAttendanceCountByStudent(studentId)

    suspend fun checkAlreadyMarked(sId: Int, stId: Int) =
        attendanceDao.checkAlreadyMarked(sId, stId)

    fun getAttendanceByStudent(studentId: Int) =
        attendanceDao.getAttendanceByStudent(studentId)
    suspend fun getAttendanceBySession(sessionId: Int) =
        attendanceDao.getAttendanceBySession(sessionId)

    suspend fun getUserById(userId: Int) =
        userDao.getUserById(userId)
}