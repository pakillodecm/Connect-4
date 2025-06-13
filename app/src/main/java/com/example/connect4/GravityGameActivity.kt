package com.example.connect4

import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.connect4.database.AppDatabase
import com.example.connect4.database.GameResult
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GravityGameActivity : ComponentActivity() {

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

    private var board = MutableList(30) { CellState.Empty }
    private lateinit var currentPlayer: CellState
    private var gameEnded = false

    private lateinit var txtStatus: TextView
    private lateinit var txtColorPlayer1: TextView
    private lateinit var txtScorePlayer1: TextView
    private lateinit var txtColorPlayer2: TextView
    private lateinit var txtScorePlayer2: TextView
    private lateinit var listButtonsColumn0: List<Button>
    private lateinit var listButtonsColumn1: List<Button>
    private lateinit var listButtonsColumn2: List<Button>
    private lateinit var listButtonsColumn3: List<Button>
    private lateinit var listButtonsColumn4: List<Button>
    private lateinit var listButtonsColumn5: List<Button>
    private lateinit var listGridButtons: List<List<Button>>
    private lateinit var listColumnSelectorButtons: List<Button>
    private lateinit var buttonReset: Button
    private lateinit var buttonChangePlayers: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gravity_game_layout)

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
        listButtonsColumn0 = listOf(
            findViewById(R.id.btn_0),
            findViewById(R.id.btn_6),
            findViewById(R.id.btn_12),
            findViewById(R.id.btn_18),
            findViewById(R.id.btn_24)
        )
        listButtonsColumn1 = listOf(
            findViewById(R.id.btn_1),
            findViewById(R.id.btn_7),
            findViewById(R.id.btn_13),
            findViewById(R.id.btn_19),
            findViewById(R.id.btn_25)
        )
        listButtonsColumn2 = listOf(
            findViewById(R.id.btn_2),
            findViewById(R.id.btn_8),
            findViewById(R.id.btn_14),
            findViewById(R.id.btn_20),
            findViewById(R.id.btn_26)
        )
        listButtonsColumn3 = listOf(
            findViewById(R.id.btn_3),
            findViewById(R.id.btn_9),
            findViewById(R.id.btn_15),
            findViewById(R.id.btn_21),
            findViewById(R.id.btn_27)
        )
        listButtonsColumn4 = listOf(
            findViewById(R.id.btn_4),
            findViewById(R.id.btn_10),
            findViewById(R.id.btn_16),
            findViewById(R.id.btn_22),
            findViewById(R.id.btn_28)
        )
        listButtonsColumn5 = listOf(
            findViewById(R.id.btn_5),
            findViewById(R.id.btn_11),
            findViewById(R.id.btn_17),
            findViewById(R.id.btn_23),
            findViewById(R.id.btn_29)
        )
        listGridButtons = listOf(
            listButtonsColumn0,
            listButtonsColumn1,
            listButtonsColumn2,
            listButtonsColumn3,
            listButtonsColumn4,
            listButtonsColumn5
        )
        listColumnSelectorButtons = listOf(
            findViewById(R.id.btn_col_0),
            findViewById(R.id.btn_col_1),
            findViewById(R.id.btn_col_2),
            findViewById(R.id.btn_col_3),
            findViewById(R.id.btn_col_4),
            findViewById(R.id.btn_col_5)
        )

        buttonReset = findViewById(R.id.btn_reset)
        buttonChangePlayers = findViewById(R.id.btn_change_players)

        txtColorPlayer1.text = "$colorPlayer1 "
        txtColorPlayer2.text = "$colorPlayer2 "

        listGridButtons.flatten().forEach { it.isClickable = false }

        listColumnSelectorButtons.forEachIndexed { indexCol, button ->
            button.setOnClickListener {
                if (listGridButtons[indexCol].any { it.text == "" } && !gameEnded) {
                    val columnLastEmpty = listGridButtons[indexCol].findLast { it.text == "" }!!
                    columnLastEmpty.text = if (currentPlayer == CellState.P1) colorPlayer1 else colorPlayer2
                    val indexLastEmpty = listGridButtons[indexCol].indexOf(columnLastEmpty)
                    board[indexLastEmpty*listGridButtons.size+indexCol] = currentPlayer
                    if (indexLastEmpty == 0) button.isClickable = false
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
                var score = "$scorePlayer1 - $scorePlayer2 "
                score += if (numDraws == 1) "($numDraws draw)" else "($numDraws draws)"
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
                        mode = "Gravity",
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
            intent.putExtra("gameMode", "G")
            startActivity(intent)
        }

    }

    private fun resetBoard() {
        if (gameEnded) {
            val (winner, _) = checkWinnerLine()
            currentPlayer = if (winner == CellState.P1) CellState.P2 else CellState.P1
        } else {
            currentPlayer = CellState.entries.filter { it != CellState.Empty }.random()
        }
        board.replaceAll { CellState.Empty }
        listColumnSelectorButtons.forEach { it.isClickable = true }
        listGridButtons.forEach { it.forEach { button ->
            button.text = ""
            button.animate().cancel()
            button.alpha = 1f
        } }
        gameEnded = false
        txtStatus.animate().cancel()
        txtStatus.alpha = 1f
        updateStatus()
    }

    private fun updateStatus() {
        val (winner, winningLine) = checkWinnerLine()
        if (winner != null) {
            gameEnded = true
            listColumnSelectorButtons.forEach { it.isClickable = false }
            if (winner != CellState.Empty) {
                animateWinningText(txtStatus)
                animateWinningTokens(winningLine!!)
            }
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

    private fun checkWinnerLine(): Pair<CellState?, List<Button>?> {
        val winPositions = listOf(
            listOf(0, 1, 2, 3), listOf(1, 2, 3, 4), listOf(2, 3, 4, 5),
            listOf(6, 7, 8, 9), listOf(7, 8, 9, 10), listOf(8, 9, 10, 11),
            listOf(12, 13, 14, 15), listOf(13, 14, 15, 16), listOf(14, 15, 16, 17),
            listOf(18, 19, 20, 21), listOf(19, 20, 21, 22), listOf(20, 21, 22, 23),
            listOf(24, 25, 26, 27), listOf(25, 26, 27, 28), listOf(26, 27, 28, 29),
            listOf(0, 6, 12, 18), listOf(6, 12, 18, 24), listOf(1, 7, 13, 19), listOf(7, 13, 19, 25),
            listOf(2, 8, 14, 20), listOf(8, 14, 20, 26), listOf(3, 9, 15, 21), listOf(9, 15 ,21, 27),
            listOf(4, 10, 16, 22), listOf(10, 16, 22, 28), listOf(5, 11, 17, 23), listOf(11, 17, 23, 29),
            listOf(0, 7, 14, 21), listOf(1, 8, 15, 22), listOf(2, 9, 16, 23),
            listOf(6, 13, 20, 27), listOf(7, 14, 21, 28), listOf(8, 15, 22, 29),
            listOf(18, 13, 8, 3), listOf(19, 14, 9, 4), listOf(20, 15, 10, 5),
            listOf(24, 19, 14, 9), listOf(25, 20, 15, 10), listOf(26, 21, 16, 11)
        )
        for ((a, b, c, d) in winPositions) {
            if (board[a] != CellState.Empty && board[a] == board[b] &&
                board[b] == board[c] && board[c] == board[d]) {
                val winningLine = listOf(a, b, c, d)
                val winningLineButtons = mutableListOf<Button>()
                for (pos in winningLine) {
                    val row = pos/6
                    val col = pos%6
                    winningLineButtons.add(listGridButtons[col][row])
                }
                return Pair(board[a], winningLineButtons)
            }
        }
        return if (board.all { it != CellState.Empty }) {
            Pair(CellState.Empty, null)
        } else {
            Pair(null, null)
        }
    }

    private fun animateWinningText(textView: TextView) {
        textView.animate()
            .alpha(1f)
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(800)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                textView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(400)
                    .start()
            }
            .start()
    }

    private fun animateWinningTokens(listButtons: List<Button>) {
        listButtons.forEach {
            blinkButton(it)
        }
    }

    private fun blinkButton(button: Button, reps: Int = 5) {
        if (reps == 0) {
            button.alpha = 1f
            return
        }
        button.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                button.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .withEndAction {
                        blinkButton(button, reps - 1)
                    }
                    .start()
            }
            .start()
    }

}
