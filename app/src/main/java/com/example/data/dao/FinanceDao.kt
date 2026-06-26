package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FinanceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<FinanceLog>>

    @Query("SELECT * FROM finance_logs WHERE type = :type ORDER BY timestamp DESC")
    fun getLogsByType(type: String): Flow<List<FinanceLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: FinanceLog)
}
