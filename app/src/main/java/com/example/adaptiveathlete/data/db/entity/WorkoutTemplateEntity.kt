package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.adaptiveathlete.data.model.WorkoutType

@Entity(tableName = "workout_templates")
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: WorkoutType,
    val description: String? = null,
    val estimatedDurationMinutes: Int,
    val difficulty: Int = 5  // 1-10 scale
)

