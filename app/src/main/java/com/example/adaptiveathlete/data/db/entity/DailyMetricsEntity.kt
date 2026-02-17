package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_metrics")
data class DailyMetricsEntity(
    @PrimaryKey
    val date: Long,  // Unix timestamp at start of day
    val bodyweight: Float? = null,
    val waistInches: Float? = null,
    val sleepHours: Float? = null,
    val painLevel: Int? = null,         // 0-10
    val energyLevel: Int? = null,       // 1-10
    val stressLevel: Int? = null,       // 1-10
    val dailyRitualComplete: Boolean = false,
    val notes: String? = null
)

