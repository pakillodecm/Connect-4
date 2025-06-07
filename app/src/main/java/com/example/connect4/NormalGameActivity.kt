package com.example.connect4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.connect4.database.AppDatabase
import com.example.connect4.database.GameResult
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Duration
import java.time.format.DateTimeFormatter

class NormalGameActivity : ComponentActivity() {

    enum class CellState { Empty, P1, P2 }

    private lateinit var namePlayer1: String
    private lateinit var namePlayer2: String
    private lateinit var colorPlayer1: String
    private lateinit var colorPlayer2: String
    private var scorePlayer1: Int = 0
    private var scorePlayer2: Int = 0
    private var numDraws: Int = 0
    private var startDateTime: LocalDateTime = LocalDateTime.now()
    private lateinit var endDateTime: LocalDateTime

    private var board = MutableList(16) { CellState.Empty }
    private lateinit var currentPlayer: CellState
    private var gameEnded = false

    private lateinit var txtStatus: TextView
    private lateinit var txtColorPlayer1: TextView
    private lateinit var txtScorePlayer1: TextView
    private lateinit var txtColorPlayer2: TextView
    private lateinit var txtScorePlayer2: TextView
    private lateinit var listButtons: List<Button>
    private lateinit var buttonReset: Button
    private lateinit var buttonChangePlayers: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.normal_game_layout)

        namePlayer1 = intent.getStringExtra("namePlayer1").toString()
        namePlayer2 = intent.getStringExtra("namePlayer2").toString()
        colorPlayer1 = intent.getStringExtra("colorPlayer1").toString()
        colorPlayer2 = intent.getStringExtra("colorPlayer2").toString()

        currentPlayer = CellState.entries.filter { it != CellState.Empty }.random()

        txtStatus = findViewById(R.id.txt_status)
        txtColorPlayer1 = findViewById(R.id.txt_color_1)
        txtScorePlayer1 = findViewById(R.id.txt_score_1)
        txtColorPlayer2 = findViewById(R.id.txt_color_2)
        txtScorePlayer2 = findViewById(R.id.txt_score_2)
        updateStatus()
        listButtons = listOf(
            findViewById(R.id.btn_0), findViewById(R.id.btn_1), findViewById(R.id.btn_2), findViewById(R.id.btn_3),
            findViewById(R.id.btn_4), findViewById(R.id.btn_5), findViewById(R.id.btn_6), findViewById(R.id.btn_7),
            findViewById(R.id.btn_8), findViewById(R.id.btn_9), findViewById(R.id.btn_10), findViewById(R.id.btn_11),
            findViewById(R.id.btn_12),findViewById(R.id.btn_13),findViewById(R.id.btn_14),findViewById(R.id.btn_15)
        )
        buttonReset = findViewById(R.id.btn_reset)
        buttonChangePlayers = findViewById(R.id.btn_change_players)

        txtColorPlayer1.text = "$colorPlayer1 "
        txtColorPlayer2.text = "$colorPlayer2 "

        listButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (board[index] == CellState.Empty && !gameEnded) {
                    button.text = if (currentPlayer == CellState.P1) colorPlayer1 else colorPlayer2
                    board[index] = currentPlayer
                    button.isClickable = false
                    currentPlayer = if (currentPlayer == CellState.P1) CellState.P2 else CellState.P1
                    updateStatus()
                }
            }
        }

        buttonReset.setOnClickListener {
            resetBoard()
        }

        buttonChangePlayers.setOnClickListener {
            if (scorePlayer1 != 0 || scorePlayer2 != 0 || numDraws != 0) {
                endDateTime = LocalDateTime.now()
                val score = if (numDraws == 1) {
                    "$scorePlayer1 - $scorePlayer2 ($numDraws draw)"
                } else {
                    "$scorePlayer1 - $scorePlayer2 ($numDraws draws)"
                }
                val winner = if (scorePlayer1 != scorePlayer2) {
                    if (scorePlayer1 > scorePlayer2) namePlayer1 else namePlayer2
                } else {
                    "Draw"
                }
                val date = startDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd - kk:mm"))
                val gameTime = Duration.between(startDateTime, endDateTime)
                val duration = "${gameTime.toMinutesPart()} min ${gameTime.toSecondsPart()} sec"

                lifecycleScope.launch {
                    val newResult = GameResult(
                        player1 = "$namePlayer1 $colorPlayer1",
                        player2 = "$namePlayer2 $colorPlayer2",
                        score = score,
                        winner = winner,
                        mode = "Normal",
                        date = date,
                        duration = duration
                    )
                    AppDatabase.getInstance(applicationContext).gameHistoryDAO().insertResult(newResult)
                }
            }

            val intent = Intent(this, PlayersChoiceActivity::class.java)
            if (namePlayer1 == "Player 1") namePlayer1 = ""
            if (namePlayer2 == "Player 2") namePlayer2 = ""
            intent.putExtra("namePlayer1", namePlayer1)
            intent.putExtra("namePlayer2", namePlayer2)
            intent.putExtra("colorPlayer1", colorPlayer1)
            intent.putExtra("colorPlayer2", colorPlayer2)
            intent.putExtra("gameMode", "N")
            startActivity(intent)
        }

    }

    private fun resetBoard() {
        if (gameEnded) {
            currentPlayer = if (checkWinner() == CellState.P1) CellState.P2 else CellState.P1
        } else {
            currentPlayer = CellState.entries.filter { it != CellState.Empty }.random()
        }
        board.replaceAll { CellState.Empty }
        listButtons.forEach {
            it.text = ""
            it.isClickable = true
        }
        gameEnded = false
        updateStatus()
    }

    private fun updateStatus() {
        val winner = checkWinner()
        if (winner != null) {
            gameEnded = true
            when (winner) {
                CellState.P1 -> {
                    txtStatus.text = "$namePlayer1 wins!"
                    scorePlayer1++
                    txtScorePlayer1.text = scorePlayer1.toString()
                }
                CellState.P2 -> {
                    txtStatus.text = "$namePlayer2 wins!"
                    scorePlayer2++
                    txtScorePlayer2.text = scorePlayer2.toString()
                }
                CellState.Empty -> {
                    txtStatus.text = "Draw"
                    numDraws++
                }
            }
        } else {
            val name = if (currentPlayer == CellState.P1) namePlayer1 else namePlayer2
            txtStatus.text = "$name's turn"
        }
    }

    private fun checkWinner(): CellState? {
        val winPositions = listOf(
            listOf(0, 1, 2, 3), listOf(4, 5, 6, 7), listOf(8, 9, 10, 11), listOf(12, 13, 14, 15),
            listOf(0, 4, 8, 12), listOf(1, 5, 9, 13), listOf(2, 6, 10, 14), listOf(3, 7, 11, 15),
            listOf(0, 5, 10, 15), listOf(3, 6, 9, 12)
        )
        for ((a, b, c, d) in winPositions) {
            if (board[a] != CellState.Empty && board[a] == board[b] &&
                board[b] == board[c] && board[c] == board[d]) {
                return board[a]
            }
        }
        return if (board.all { it != CellState.Empty }) CellState.Empty else null
    }

}
