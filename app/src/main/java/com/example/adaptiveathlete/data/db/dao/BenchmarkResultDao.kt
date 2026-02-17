package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.BenchmarkResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BenchmarkResultDao {

    @Query("SELECT * FROM benchmark_results ORDER BY completedAt DESC")
    fun getAllResults(): Flow<List<BenchmarkResultEntity>>

    @Query("SELECT * FROM benchmark_results WHERE benchmarkType = :type ORDER BY completedAt DESC LIMIT 1")
    suspend fun getLatestResultByType(type: String): BenchmarkResultEntity?

    @Query("SELECT * FROM benchmark_results WHERE benchmarkType = :type ORDER BY completedAt DESC")
    fun getResultsByType(type: String): Flow<List<BenchmarkResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: BenchmarkResultEntity): Long

    @Query("DELETE FROM benchmark_results WHERE id = :id")
    suspend fun deleteResult(id: Long)
}

