package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun getSessionByIdFlow(sessionId: Long): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE endedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getActiveSession(): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE endedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    fun getActiveSessionFlow(): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE templateId = :templateId AND endedAt IS NOT NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getLastCompletedSessionForTemplate(templateId: Long): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE startedAt >= :startTime AND startedAt < :endTime ORDER BY startedAt DESC")
    suspend fun getSessionsInRange(startTime: Long, endTime: Long): List<WorkoutSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Delete
    suspend fun deleteSession(session: WorkoutSessionEntity)
}

