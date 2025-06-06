package com.example.connect4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connect4.database.AppDatabase
import com.example.connect4.database.GameHistoryDAO
import kotlinx.coroutines.launch

class HistoryActivity : ComponentActivity() {

    private lateinit var namePlayer1: String
    private lateinit var namePlayer2: String
    private lateinit var colorPlayer1: String
    private lateinit var colorPlayer2: String
    private lateinit var gameMode: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameResultAdapter
    private lateinit var dao: GameHistoryDAO

    private lateinit var buttonCloseHistory: Button
    private lateinit var buttonClearHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_layout)

        recyclerView = findViewById(R.id.recycler_view_history)
        adapter = GameResultAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val db = AppDatabase.getInstance(applicationContext)
        dao = db.gameHistoryDAO()

        lifecycleScope.launch {
            dao.getAllResults().collect { results -> adapter.updateData(results) }
        }

        buttonCloseHistory = findViewById(R.id.btn_close_history)
        buttonClearHistory = findViewById(R.id.btn_clear_history)

        namePlayer1 = intent.getStringExtra("namePlayer1").toString()
        namePlayer1 = if (namePlayer1 == "Player 1") "" else namePlayer1
        namePlayer2 = intent.getStringExtra("namePlayer2").toString()
        namePlayer2 = if (namePlayer2 == "Player 2") "" else namePlayer2
        colorPlayer1 = intent.getStringExtra("colorPlayer1").toString()
        colorPlayer2 = intent.getStringExtra("colorPlayer2").toString()
        gameMode = intent.getStringExtra("gameMode").toString()

        buttonCloseHistory.setOnClickListener {
            val targetActivity = PlayersChoiceActivity::class.java
            val intent = Intent(this, targetActivity)
            intent.putExtra("namePlayer1", namePlayer1)
            intent.putExtra("namePlayer2", namePlayer2)
            intent.putExtra("colorPlayer1", colorPlayer1)
            intent.putExtra("colorPlayer2", colorPlayer2)
            intent.putExtra("gameMode", gameMode)
            startActivity(intent)
        }

        buttonClearHistory.setOnClickListener {
            lifecycleScope.launch {
                dao.clearAllResults()
            }
        }

    }

}
