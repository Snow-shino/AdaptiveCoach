package com.example.adaptiveathlete.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.adaptiveathlete.data.model.ExerciseType
import com.example.adaptiveathlete.data.model.MuscleGroup
import com.example.adaptiveathlete.data.model.EquipmentType

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: ExerciseType,
    val primaryMuscles: List<MuscleGroup>,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val equipment: EquipmentType,
    val instructions: String? = null,
    val videoUrl: String? = null
)

