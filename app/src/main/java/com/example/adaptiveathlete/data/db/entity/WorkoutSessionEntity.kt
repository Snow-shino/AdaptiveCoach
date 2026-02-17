package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("templateId"), Index("startedAt")]
)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateId: Long?,
    val startedAt: Long,                // Unix timestamp
    val endedAt: Long? = null,          // Unix timestamp, null if in progress
    val rpe: Int? = null,               // Subjective effort 1-10
    val notes: String? = null,
    val location: String? = null        // Gym name, home, crag, etc.
)

