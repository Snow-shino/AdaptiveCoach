package com.example.adaptiveathlete.data.db

import androidx.room.TypeConverter
import com.example.adaptiveathlete.data.model.ExerciseType
import com.example.adaptiveathlete.data.model.MuscleGroup
import com.example.adaptiveathlete.data.model.WorkoutType
import com.example.adaptiveathlete.data.model.EquipmentType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, dateFormatter) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        return value.split(",")
    }

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

