
package com.student.qrattendanceapp.data.dao

import androidx.room.*
import com.student.qrattendanceapp.data.entities.Session

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun updateSession(session: Session)

    @Query("SELECT * FROM sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Int): Session?

    @Query("UPDATE sessions SET isActive = 0 WHERE id = :sessionId")
    suspend fun endSession(sessionId: Int)
}