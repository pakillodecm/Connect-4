package com.example.connect4.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val player1: String,
    val player2: String,
    val score: String,
    val winner: String,
    val mode: String,
    val date: String,
    val duration: String
)