package com.example.connect4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameResult::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameHistoryDAO(): GameHistoryDAO

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "connect4-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
