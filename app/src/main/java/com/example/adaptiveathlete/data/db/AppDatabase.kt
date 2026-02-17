package com.example.adaptiveathlete.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.adaptiveathlete.data.db.dao.*
import com.example.adaptiveathlete.data.db.entity.*

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutTemplateEntity::class,
        TemplateExerciseEntity::class,
        SetPlanEntity::class,
        WorkoutSessionEntity::class,
        SetEntryEntity::class,
        DailyMetricsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao
    abstract fun setPlanDao(): SetPlanDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun setEntryDao(): SetEntryDao
    abstract fun dailyMetricsDao(): DailyMetricsDao
}

