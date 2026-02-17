package com.example.adaptiveathlete.data.repository

import com.example.adaptiveathlete.data.db.dao.BenchmarkResultDao
import com.example.adaptiveathlete.data.db.entity.BenchmarkResultEntity
import com.example.adaptiveathlete.ui.screens.BenchmarkData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BenchmarkRepository @Inject constructor(
    private val benchmarkResultDao: BenchmarkResultDao
) {

    fun getAllResults(): Flow<List<BenchmarkResultEntity>> {
        return benchmarkResultDao.getAllResults()
    }

    fun getResultsByType(type: String): Flow<List<BenchmarkResultEntity>> {
        return benchmarkResultDao.getResultsByType(type)
    }

    suspend fun getLatestResultByType(type: String): BenchmarkResultEntity? {
        return benchmarkResultDao.getLatestResultByType(type)
    }

    suspend fun saveLiftingBenchmark(data: BenchmarkData.Lifting): Long {
        val entity = BenchmarkResultEntity(
            benchmarkType = "LIFTING",
            bodyweight = data.bodyweight,
            pullUpMaxReps = data.pullUpMaxReps,
            pullUp5RMWeight = data.pullUp5RMWeight,
            pushUpMaxReps = data.pushUpMaxReps,
            leg5RMWeight = data.leg5RMWeight,
            deadHangTime = data.deadHangTime,
            hollowHoldTime = data.hollowHoldTime
        )
        return benchmarkResultDao.insertResult(entity)
    }

    suspend fun saveClimbingBenchmark(data: BenchmarkData.Climbing): Long {
        val entity = BenchmarkResultEntity(
            benchmarkType = "CLIMBING",
            maxVGrade = data.maxVGrade,
            attemptedVGrade = data.attemptedVGrade,
            maxHangWeight = data.maxHangWeight,
            maxHangTime = data.maxHangTime,
            repeatersCompleted = data.repeatersCompleted
        )
        return benchmarkResultDao.insertResult(entity)
    }

    suspend fun saveRunningBenchmark(data: BenchmarkData.Running): Long {
        val entity = BenchmarkResultEntity(
            benchmarkType = "RUNNING",
            mileTime = data.mileTime,
            cooperDistance = data.cooperDistance
        )
        return benchmarkResultDao.insertResult(entity)
    }

    suspend fun deleteResult(id: Long) {
        benchmarkResultDao.deleteResult(id)
    }
}

