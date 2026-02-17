package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents a training program (e.g., "V6 Athlete Program", "Beginner Climber", etc.)
 */
@Entity(tableName = "programs")
data class ProgramEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val durationWeeks: Int,
    val daysPerWeek: Int,
    val difficulty: Int, // 1-10
    val goals: List<String>, // e.g., ["strength", "power", "fat_loss", "v6_climbing"]
    val isActive: Boolean = false,
    val startDate: LocalDate? = null,
    val createdAt: Long = System.currentTimeMillis()
)

