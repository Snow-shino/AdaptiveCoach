package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.TemplateExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateExerciseDao {

    @Query("SELECT * FROM template_exercises WHERE workoutTemplateId = :templateId ORDER BY orderIndex ASC")
    fun getExercisesForTemplate(templateId: Long): Flow<List<TemplateExerciseEntity>>

    @Query("SELECT * FROM template_exercises WHERE workoutTemplateId = :templateId ORDER BY orderIndex ASC")
    suspend fun getExercisesForTemplateSync(templateId: Long): List<TemplateExerciseEntity>

    @Query("SELECT * FROM template_exercises WHERE id = :id")
    suspend fun getTemplateExerciseById(id: Long): TemplateExerciseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercise(templateExercise: TemplateExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercises(templateExercises: List<TemplateExerciseEntity>)

    @Update
    suspend fun updateTemplateExercise(templateExercise: TemplateExerciseEntity)

    @Delete
    suspend fun deleteTemplateExercise(templateExercise: TemplateExerciseEntity)

    @Query("DELETE FROM template_exercises WHERE workoutTemplateId = :templateId")
    suspend fun deleteAllForTemplate(templateId: Long)
}

