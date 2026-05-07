package com.student.qrattendanceapp.data.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.student.qrattendanceapp.data.entities.ClassRoom

@Dao
interface ClassRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classRoom: ClassRoom)

    @Delete
    suspend fun deleteClass(classRoom: ClassRoom)

    @Query("SELECT * FROM classes WHERE teacherId = :teacherId")
    fun getClassesByTeacher(teacherId: Int): LiveData<List<ClassRoom>>

    @Query("SELECT * FROM classes WHERE classCode = :code LIMIT 1")
    suspend fun getClassByCode(code: String): ClassRoom?
}