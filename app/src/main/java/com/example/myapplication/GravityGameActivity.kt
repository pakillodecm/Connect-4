package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class GravityGameActivity : ComponentActivity() {

    enum class CellState { Empty, P1, P2 }

    private lateinit var namePlayer1: String
    private lateinit var namePlayer2: String
    private lateinit var colorPlayer1: String
    private lateinit var colorPlayer2: String

    private var board = MutableList(30) { CellState.Empty }
    private lateinit var currentPlayer: CellState
    private var gameEnded = false

    private lateinit var txtStatus: TextView
    private lateinit var listButtonsColumn0: List<Button>
    private lateinit var listButtonsColumn1: List<Button>
    private lateinit var listButtonsColumn2: List<Button>
    private lateinit var listButtonsColumn3: List<Button>
    private lateinit var listButtonsColumn4: List<Button>
    private lateinit var listButtonsColumn5: List<Button>
    private lateinit var listColumns: List<List<Button>>
    private lateinit var listButtons: List<Button>
    private lateinit var buttonReset: Button
    private lateinit var buttonChangePlayers: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.gravity_game_layout)

        namePlayer1 = intent.getStringExtra("namePlayer1").toString()
        namePlayer2 = intent.getStringExtra("namePlayer2").toString()
        colorPlayer1 = intent.getStringExtra("colorPlayer1").toString()
        colorPlayer2 = intent.getStringExtra("colorPlayer2").toString()

        currentPlayer = CellState.entries.filter { it != CellState.Empty }.random()

        txtStatus = findViewById(R.id.txt_status)
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
        listColumns = listOf(
            listButtonsColumn0,
            listButtonsColumn1,
            listButtonsColumn2,
            listButtonsColumn3,
            listButtonsColumn4,
            listButtonsColumn5
        )
        listButtons = listOf(
            findViewById(R.id.btn_col_0),
            findViewById(R.id.btn_col_1),
            findViewById(R.id.btn_col_2),
            findViewById(R.id.btn_col_3),
            findViewById(R.id.btn_col_4),
            findViewById(R.id.btn_col_5)
        )

        buttonReset = findViewById(R.id.btn_reset)
        buttonChangePlayers = findViewById(R.id.btn_change_players)

        listButtons.forEachIndexed { indexCol, button ->
            button.setOnClickListener {
                if (listColumns[indexCol].any { it.text == "" } && !gameEnded) {
                    val columnLastEmpty = listColumns[indexCol].findLast { it.text == "" }!!
                    columnLastEmpty.text = if (currentPlayer == CellState.P1) colorPlayer1 else colorPlayer2
                    val indexLastEmpty = listColumns[indexCol].indexOf(columnLastEmpty)
                    board[indexLastEmpty*listColumns.size+indexCol] = currentPlayer
                    currentPlayer = if (currentPlayer == CellState.P1) CellState.P2 else CellState.P1
                    updateStatus()
                }
            }
        }

//        listColumns.forEachIndexed { indexCol, column ->
//            column.forEachIndexed { indexBtn, button ->
//                button.setOnClickListener {
//                    if (listColumns[indexCol].any { it.text == "" } && !gameEnded) {
//                        val columnLastEmpty = listColumns[indexCol].findLast { it.text == "" }!!
//                        columnLastEmpty.text = if (currentPlayer == CellState.P1) colorPlayer1 else colorPlayer2
//                        val indexLastEmpty = listColumns[indexCol].indexOf(columnLastEmpty)
//                        board[indexLastEmpty*listColumns.size+indexCol] = currentPlayer
//                        currentPlayer = if (currentPlayer == CellState.P1) CellState.P2 else CellState.P1
//                        updateStatus()
//                    }
//                }
//            }
//        }

        buttonReset.setOnClickListener {
            resetBoard()
        }

        buttonChangePlayers.setOnClickListener {
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
            currentPlayer = if (checkWinner() == CellState.P1) CellState.P2 else CellState.P1
        } else {
            currentPlayer = CellState.entries.filter { it != CellState.Empty }.random()
        }
        board.replaceAll { CellState.Empty }
        for (column in listColumns) {
            column.forEach { it.text = "" }
        }
        gameEnded = false
        updateStatus()
    }

    private fun updateStatus() {
        val winner = checkWinner()
        if (winner != null) {
            gameEnded = true
            when (winner) {
                CellState.P1 -> txtStatus.text = "$namePlayer1 wins!"
                CellState.P2 -> txtStatus.text = "$namePlayer2 wins!"
                CellState.Empty -> txtStatus.text = "Draw"
            }
        } else {
            val name = if (currentPlayer == CellState.P1) namePlayer1 else namePlayer2
            txtStatus.text = "$name's turn"
        }
    }

    private fun checkWinner(): CellState? {
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
                return board[a]
            }
        }
        return if (board.all { it != CellState.Empty }) CellState.Empty else null
    }

}
