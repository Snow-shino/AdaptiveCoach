package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents a scheduled workout day in the calendar
 */
@Entity(
    tableName = "scheduled_workouts",
    foreignKeys = [
        ForeignKey(
            entity = ProgramEntity::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("programId"), Index("templateId"), Index("scheduledDate")]
)
data class ScheduledWorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: Long,
    val templateId: Long,
    val scheduledDate: LocalDate,
    val weekNumber: Int, // Which week of the program (1-4)
    val dayOfWeek: Int, // 1=Monday, 7=Sunday
    val isCompleted: Boolean = false,
    val isSkipped: Boolean = false,
    val completedAt: Long? = null,
    val notes: String? = null
)

