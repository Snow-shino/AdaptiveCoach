package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.SetPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetPlanDao {

    @Query("SELECT * FROM set_plans WHERE templateExerciseId = :templateExerciseId ORDER BY setNumber ASC")
    fun getSetPlansForTemplateExercise(templateExerciseId: Long): Flow<List<SetPlanEntity>>

    @Query("SELECT * FROM set_plans WHERE templateExerciseId = :templateExerciseId ORDER BY setNumber ASC")
    suspend fun getSetPlansForTemplateExerciseSync(templateExerciseId: Long): List<SetPlanEntity>

    @Query("SELECT * FROM set_plans WHERE id = :id")
    suspend fun getSetPlanById(id: Long): SetPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetPlan(setPlan: SetPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetPlans(setPlans: List<SetPlanEntity>)

    @Update
    suspend fun updateSetPlan(setPlan: SetPlanEntity)

    @Delete
    suspend fun deleteSetPlan(setPlan: SetPlanEntity)

    @Query("DELETE FROM set_plans WHERE templateExerciseId = :templateExerciseId")
    suspend fun deleteAllForTemplateExercise(templateExerciseId: Long)
}

