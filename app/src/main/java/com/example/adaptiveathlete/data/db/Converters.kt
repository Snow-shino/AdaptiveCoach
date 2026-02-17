package com.example.adaptiveathlete.data.db

import androidx.room.TypeConverter
import com.example.adaptiveathlete.data.model.ExerciseType
import com.example.adaptiveathlete.data.model.MuscleGroup
import com.example.adaptiveathlete.data.model.WorkoutType
import com.example.adaptiveathlete.data.model.EquipmentType

class Converters {

    @TypeConverter
    fun fromExerciseType(value: ExerciseType): String = value.name

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType = ExerciseType.valueOf(value)

    @TypeConverter
    fun fromWorkoutType(value: WorkoutType): String = value.name

    @TypeConverter
    fun toWorkoutType(value: String): WorkoutType = WorkoutType.valueOf(value)

    @TypeConverter
    fun fromEquipmentType(value: EquipmentType): String = value.name

    @TypeConverter
    fun toEquipmentType(value: String): EquipmentType = EquipmentType.valueOf(value)

    @TypeConverter
    fun fromMuscleGroupList(value: List<MuscleGroup>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toMuscleGroupList(value: String): List<MuscleGroup> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { MuscleGroup.valueOf(it) }
    }
}

