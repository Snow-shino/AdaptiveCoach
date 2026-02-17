package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.WorkoutTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTemplateDao {

    @Query("SELECT * FROM workout_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>>

    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: Long): WorkoutTemplateEntity?

    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    fun getTemplateByIdFlow(templateId: Long): Flow<WorkoutTemplateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<WorkoutTemplateEntity>)

    @Update
    suspend fun updateTemplate(template: WorkoutTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplateEntity)
}

