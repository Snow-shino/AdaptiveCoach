package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.ScheduledWorkoutEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ScheduledWorkoutDao {
    @Insert
    suspend fun insert(scheduledWorkout: ScheduledWorkoutEntity): Long

    @Insert
    suspend fun insertAll(scheduledWorkouts: List<ScheduledWorkoutEntity>)

    @Update
    suspend fun update(scheduledWorkout: ScheduledWorkoutEntity)

    @Delete
    suspend fun delete(scheduledWorkout: ScheduledWorkoutEntity)

    @Query("SELECT * FROM scheduled_workouts WHERE id = :id")
    suspend fun getById(id: Long): ScheduledWorkoutEntity?

    @Query("SELECT * FROM scheduled_workouts WHERE programId = :programId ORDER BY scheduledDate ASC")
    fun getByProgram(programId: Long): Flow<List<ScheduledWorkoutEntity>>

    @Query("SELECT * FROM scheduled_workouts WHERE scheduledDate = :date ORDER BY dayOfWeek ASC")
    fun getByDate(date: LocalDate): Flow<List<ScheduledWorkoutEntity>>

    @Query("SELECT * FROM scheduled_workouts WHERE scheduledDate BETWEEN :startDate AND :endDate ORDER BY scheduledDate ASC")
    fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<ScheduledWorkoutEntity>>

    @Query("SELECT * FROM scheduled_workouts WHERE programId = :programId AND weekNumber = :weekNumber ORDER BY dayOfWeek ASC")
    fun getByProgramWeek(programId: Long, weekNumber: Int): Flow<List<ScheduledWorkoutEntity>>

    @Query("UPDATE scheduled_workouts SET isCompleted = 1, completedAt = :completedAt WHERE id = :id")
    suspend fun markComplete(id: Long, completedAt: Long)

    @Query("UPDATE scheduled_workouts SET isSkipped = 1 WHERE id = :id")
    suspend fun markSkipped(id: Long)

    @Query("DELETE FROM scheduled_workouts WHERE programId = :programId")
    suspend fun deleteByProgram(programId: Long)

    @Query("SELECT COUNT(*) FROM scheduled_workouts WHERE programId = :programId AND isCompleted = 1")
    suspend fun getCompletedCount(programId: Long): Int

    @Query("SELECT COUNT(*) FROM scheduled_workouts WHERE programId = :programId")
    suspend fun getTotalCount(programId: Long): Int
}

