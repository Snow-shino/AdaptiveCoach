package com.example.adaptiveathlete.data.db.dao

import androidx.room.*
import com.example.adaptiveathlete.data.db.entity.SetEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetEntryDao {

    @Query("SELECT * FROM set_entries WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getEntriesForSession(sessionId: Long): Flow<List<SetEntryEntity>>

    @Query("SELECT * FROM set_entries WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getEntriesForSessionSync(sessionId: Long): List<SetEntryEntity>

    @Query("SELECT * FROM set_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): SetEntryEntity?

    @Query("SELECT * FROM set_entries WHERE setPlanId = :setPlanId AND isDone = 1 ORDER BY completedAt DESC LIMIT 1")
    suspend fun getLastCompletedEntryForSetPlan(setPlanId: Long): SetEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: SetEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<SetEntryEntity>)

    @Update
    suspend fun updateEntry(entry: SetEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: SetEntryEntity)

    @Query("DELETE FROM set_entries WHERE sessionId = :sessionId")
    suspend fun deleteAllForSession(sessionId: Long)
}

