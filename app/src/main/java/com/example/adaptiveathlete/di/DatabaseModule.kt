package com.example.adaptiveathlete.di

import android.content.Context
import androidx.room.Room
import com.example.adaptiveathlete.data.db.AppDatabase
import com.example.adaptiveathlete.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "adaptive_athlete_db"
        )
            .fallbackToDestructiveMigration()  // For development; remove for production
            .build()
    }

    @Provides
    fun provideExerciseDao(database: AppDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    fun provideWorkoutTemplateDao(database: AppDatabase): WorkoutTemplateDao {
        return database.workoutTemplateDao()
    }

    @Provides
    fun provideTemplateExerciseDao(database: AppDatabase): TemplateExerciseDao {
        return database.templateExerciseDao()
    }

    @Provides
    fun provideSetPlanDao(database: AppDatabase): SetPlanDao {
        return database.setPlanDao()
    }

    @Provides
    fun provideWorkoutSessionDao(database: AppDatabase): WorkoutSessionDao {
        return database.workoutSessionDao()
    }

    @Provides
    fun provideSetEntryDao(database: AppDatabase): SetEntryDao {
        return database.setEntryDao()
    }

    @Provides
    fun provideDailyMetricsDao(database: AppDatabase): DailyMetricsDao {
        return database.dailyMetricsDao()
    }

    @Provides
    fun provideBenchmarkResultDao(database: AppDatabase): BenchmarkResultDao {
        return database.benchmarkResultDao()
    }

    @Provides
    fun provideProgramDao(database: AppDatabase): ProgramDao {
        return database.programDao()
    }

    @Provides
    fun provideScheduledWorkoutDao(database: AppDatabase): ScheduledWorkoutDao {
        return database.scheduledWorkoutDao()
    }
}

