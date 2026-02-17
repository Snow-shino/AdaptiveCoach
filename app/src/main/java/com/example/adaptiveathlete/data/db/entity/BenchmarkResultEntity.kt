package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "benchmark_results")
data class BenchmarkResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val benchmarkType: String, // "LIFTING", "CLIMBING", "RUNNING"
    val bodyweight: Float? = null,

    // Lifting metrics
    val pullUpMaxReps: Int? = null,
    val pullUp5RMWeight: Float? = null,
    val pushUpMaxReps: Int? = null,
    val leg5RMWeight: Float? = null,
    val deadHangTime: Int? = null, // seconds
    val hollowHoldTime: Int? = null, // seconds

    // Climbing metrics
    val maxVGrade: String? = null,
    val attemptedVGrade: String? = null,
    val maxHangWeight: Float? = null,
    val maxHangTime: Int? = null, // seconds
    val repeatersCompleted: Boolean? = null,

    // Running metrics
    val mileTime: Int? = null, // seconds
    val cooperDistance: Float? = null, // meters

    val completedAt: LocalDateTime = LocalDateTime.now(),
    val notes: String? = null
)

