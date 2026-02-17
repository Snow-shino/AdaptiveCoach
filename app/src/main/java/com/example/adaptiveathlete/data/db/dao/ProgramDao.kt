package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.ProgramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {
    @Insert
    suspend fun insert(program: ProgramEntity): Long

    @Update
    suspend fun update(program: ProgramEntity)

    @Delete
    suspend fun delete(program: ProgramEntity)

    @Query("SELECT * FROM programs WHERE id = :id")
    suspend fun getById(id: Long): ProgramEntity?

    @Query("SELECT * FROM programs WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<ProgramEntity?>

    @Query("SELECT * FROM programs ORDER BY createdAt DESC")
    fun getAllPrograms(): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveProgram(): ProgramEntity?

    @Query("SELECT * FROM programs WHERE isActive = 1 LIMIT 1")
    fun getActiveProgramFlow(): Flow<ProgramEntity?>

    @Query("UPDATE programs SET isActive = 0")
    suspend fun deactivateAllPrograms()

    @Query("UPDATE programs SET isActive = 1 WHERE id = :programId")
    suspend fun setActiveProgram(programId: Long)
}

