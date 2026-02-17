package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "set_entries",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SetPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["setPlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("setPlanId")]
)
data class SetEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val setPlanId: Long,
    val actualReps: Int? = null,
    val actualSeconds: Int? = null,
    val actualWeight: Float? = null,
    val isDone: Boolean = false,
    val completedAt: Long? = null,      // Unix timestamp
    val rpe: Int? = null,               // Per-set RPE if user wants to track
    val painLevel: Int? = null          // 0-10, track joint pain
)

