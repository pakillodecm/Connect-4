package com.example.connect4.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namePlayer1: String,
    val namePlayer2: String,
    val score: String,
    val date: String,
    val mode: String,
    val winner: String,
    val duration: String
)