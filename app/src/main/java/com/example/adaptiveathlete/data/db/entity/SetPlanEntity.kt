package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "set_plans",
    foreignKeys = [
        ForeignKey(
            entity = TemplateExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("templateExerciseId")]
)
data class SetPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateExerciseId: Long,
    val setNumber: Int,
    val targetReps: Int? = null,        // For rep-based exercises
    val targetSeconds: Int? = null,     // For timed exercises
    val targetWeight: Float? = null,    // For weighted exercises (in pounds)
    val restSeconds: Int = 90,          // Rest time after this set
    val rpe: Int? = null                // Target RPE (1-10) if specified
)

