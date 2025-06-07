package com.example.connect4.database

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameHistoryDAO {

    @Insert
    suspend fun insertResult(result: GameResult)

    @Query("SELECT * FROM GameResult ORDER BY id DESC")
    fun getAllResults(): Flow<List<GameResult>>

    @Query("DELETE FROM GameResult")
    suspend fun clearAllResults()

}
