package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "template_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutTemplateId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workoutTemplateId"),
        Index("exerciseId")
    ]
)
data class TemplateExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutTemplateId: Long,
    val exerciseId: Long,
    val orderIndex: Int,
    val notes: String? = null,  // Form cues, technique reminders
    val supersetGroup: Int? = null  // Exercises with same number are supersetted
)

