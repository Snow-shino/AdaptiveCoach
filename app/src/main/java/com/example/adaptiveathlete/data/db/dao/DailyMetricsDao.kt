package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.DailyMetricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyMetricsDao {

    @Query("SELECT * FROM daily_metrics ORDER BY date DESC")
    fun getAllMetrics(): Flow<List<DailyMetricsEntity>>

    @Query("SELECT * FROM daily_metrics WHERE date = :date")
    suspend fun getMetricsForDate(date: Long): DailyMetricsEntity?

    @Query("SELECT * FROM daily_metrics WHERE date = :date")
    fun getMetricsForDateFlow(date: Long): Flow<DailyMetricsEntity?>

    @Query("SELECT * FROM daily_metrics WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getMetricsInRange(startDate: Long, endDate: Long): List<DailyMetricsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrics(metrics: DailyMetricsEntity)

    @Update
    suspend fun updateMetrics(metrics: DailyMetricsEntity)

    @Delete
    suspend fun deleteMetrics(metrics: DailyMetricsEntity)
}

