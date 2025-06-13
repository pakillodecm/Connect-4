package com.example.connect4

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connect4.database.AppDatabase
import com.example.connect4.database.GameHistoryDAO
import kotlinx.coroutines.launch

class PlayersChoiceActivity : ComponentActivity() {

    private lateinit var namePlayer1: String
    private lateinit var namePlayer2: String
    private lateinit var colorPlayer1: String
    private lateinit var colorPlayer2: String
    private lateinit var gameMode: String

    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var adapter: GameResultAdapter
    private lateinit var dao: GameHistoryDAO

    private lateinit var txtEmptyHistory: TextView

    private lateinit var etNamePlayer1: EditText
    private lateinit var etNamePlayer2: EditText
    private lateinit var spinnerColorPlayer1: Spinner
    private lateinit var spinnerColorPlayer2: Spinner
    private lateinit var switchGameMode: Switch
    private lateinit var txtNormalMode: TextView
    private lateinit var txtGravityMode: TextView
    private lateinit var buttonStartGame: Button
    private lateinit var txtNamesError: TextView
    private lateinit var txtColorsError: TextView
    private lateinit var buttonShowHistory: Button
    private lateinit var buttonShowDescription: Button
    private lateinit var buttonShowAbout: Button
    private lateinit var popUpHistory: RelativeLayout
    private lateinit var popUpConfirmation: RelativeLayout
    private lateinit var popUpDescriptionNormal: RelativeLayout
    private lateinit var popUpDescriptionGravity: RelativeLayout
    private lateinit var popUpAbout: RelativeLayout
    private lateinit var buttonDismissAction: Button
    private lateinit var buttonConfirmAction: Button
    private lateinit var buttonCloseHistory: Button
    private lateinit var buttonClearHistory: Button
    private lateinit var buttonCloseDescription: Button
    private lateinit var buttonCloseAbout: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.players_layout)

        etNamePlayer1 = findViewById(R.id.et_name_player1)
        etNamePlayer2 = findViewById(R.id.et_name_player2)
        spinnerColorPlayer1 = findViewById(R.id.sp_color_player1)
        spinnerColorPlayer2 = findViewById(R.id.sp_color_player2)
        switchGameMode = findViewById(R.id.sw_game_mode)
        txtNormalMode = findViewById(R.id.txt_normal_mode)
        txtGravityMode = findViewById(R.id.txt_gravity_mode)
        buttonStartGame = findViewById(R.id.btn_start_game)
        txtNamesError = findViewById(R.id.txt_names_error)
        txtColorsError = findViewById(R.id.txt_colors_error)
        buttonShowHistory = findViewById(R.id.btn_show_history)
        buttonShowDescription = findViewById(R.id.btn_show_description)
        buttonShowAbout = findViewById(R.id.btn_show_about)
        popUpHistory = findViewById(R.id.popUp_history)
        popUpConfirmation = findViewById(R.id.popUp_confirmation)
        popUpDescriptionNormal = findViewById(R.id.popUp_description_normal)
        popUpDescriptionGravity = findViewById(R.id.popUp_description_gravity)
        popUpAbout = findViewById(R.id.popUp_about)

        val colors = listOf("\uD83D\uDD34", "\uD83D\uDFE0", "\uD83D\uDFE1", "\uD83D\uDFE2",
            "\uD83D\uDD35", "\uD83D\uDFE3", "\uD83D\uDFE4", "⚫", "⚪")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColorPlayer1.adapter = colorAdapter
        spinnerColorPlayer2.adapter = colorAdapter

        setStartingVariables(colors)

        switchGameMode.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                txtNormalMode.setTextColor(getColor(R.color.text_secondary))
                txtGravityMode.setTextColor(getColor(R.color.text_primary))
            } else {
                txtNormalMode.setTextColor(getColor(R.color.text_primary))
                txtGravityMode.setTextColor(getColor(R.color.text_secondary))
            }
        }

        buttonStartGame.setOnClickListener {
            val namePlayer1 = etNamePlayer1.text.toString().ifBlank { "Player 1" }
            val namePlayer2 = etNamePlayer2.text.toString().ifBlank { "Player 2" }
            val colorPlayer1 = spinnerColorPlayer1.selectedItem as String
            val colorPlayer2 = spinnerColorPlayer2.selectedItem as String
            startGame(namePlayer1, namePlayer2, colorPlayer1, colorPlayer2)
        }

        buttonShowDescription.setOnClickListener {
            if (switchGameMode.isChecked) {
                popUpDescriptionGravity.visibility = LinearLayout.VISIBLE
                buttonCloseDescription = popUpDescriptionGravity.findViewById(R.id.btn_close_description)
            } else {
                popUpDescriptionNormal.visibility = LinearLayout.VISIBLE
                buttonCloseDescription = popUpDescriptionNormal.findViewById(R.id.btn_close_description)
            }
            buttonCloseDescription.setOnClickListener {
                popUpDescriptionGravity.visibility = LinearLayout.GONE
                popUpDescriptionNormal.visibility = LinearLayout.GONE
            }
        }

        buttonShowAbout.setOnClickListener {
            popUpAbout.visibility = LinearLayout.VISIBLE
            buttonCloseAbout = popUpAbout.findViewById(R.id.btn_close_about)
            buttonCloseAbout.setOnClickListener {
                popUpAbout.visibility = LinearLayout.GONE
            }
        }

        recyclerViewHistory = popUpHistory.findViewById(R.id.recycler_view_history)
        adapter = GameResultAdapter(emptyList())
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = adapter

        val db = AppDatabase.getInstance(applicationContext)
        dao = db.gameHistoryDAO()

        txtEmptyHistory = popUpHistory.findViewById(R.id.txt_empty_history)

        buttonShowHistory.setOnClickListener {
            lifecycleScope.launch {
                dao.getAllResults().collect { results ->
                    adapter.updateData(results)
                    if (results.isEmpty()) {
                        recyclerViewHistory.visibility = RecyclerView.GONE
                        txtEmptyHistory.visibility = TextView.VISIBLE
                    } else {
                        recyclerViewHistory.visibility = RecyclerView.VISIBLE
                        txtEmptyHistory.visibility = TextView.GONE
                    }
                }
            }
            popUpHistory.visibility = LinearLayout.VISIBLE
            buttonCloseHistory = popUpHistory.findViewById(R.id.btn_close_history)
            buttonCloseHistory.setOnClickListener {
                popUpHistory.visibility = LinearLayout.GONE
            }
            buttonClearHistory = popUpHistory.findViewById(R.id.btn_clear_history)
            buttonClearHistory.setOnClickListener {
                popUpConfirmation.visibility = LinearLayout.VISIBLE
                buttonDismissAction = popUpConfirmation.findViewById(R.id.btn_dismiss_action)
                buttonDismissAction.setOnClickListener {
                    popUpConfirmation.visibility = LinearLayout.GONE
                }
                buttonConfirmAction = popUpConfirmation.findViewById(R.id.btn_confirm_action)
                buttonConfirmAction.setOnClickListener {
                    lifecycleScope.launch {
                        dao.clearAllResults()
                    }
                    popUpConfirmation.visibility = LinearLayout.GONE
                }
            }
        }

    }

    private fun setStartingVariables(colors: List<String>) {
        namePlayer1 = intent.getStringExtra("namePlayer1") ?: ""
        namePlayer2 = intent.getStringExtra("namePlayer2") ?: ""
        colorPlayer1 = intent.getStringExtra("colorPlayer1") ?: colors[1]
        colorPlayer2 = intent.getStringExtra("colorPlayer2") ?: colors[7]
        gameMode = intent.getStringExtra("gameMode") ?: "N"
        etNamePlayer1.setText(namePlayer1)
        etNamePlayer2.setText(namePlayer2)
        spinnerColorPlayer1.setSelection(colors.indexOf(colorPlayer1))
        spinnerColorPlayer2.setSelection(colors.indexOf(colorPlayer2))
        switchGameMode.isChecked = gameMode == "G"
        if (switchGameMode.isChecked) {
            txtNormalMode.setTextColor(getColor(R.color.text_secondary))
            txtGravityMode.setTextColor(getColor(R.color.text_primary))
        }
    }

    private fun startGame(namePlayer1: String, namePlayer2: String, colorPlayer1: String, colorPlayer2: String) {
        val sameNames = namePlayer1 == namePlayer2
        val sameColors = colorPlayer1 == colorPlayer2
        txtNamesError.visibility = if (sameNames) TextView.VISIBLE else TextView.GONE
        txtColorsError.visibility = if (sameColors) TextView.VISIBLE else TextView.GONE
        if (!sameNames && !sameColors) {
            val targetActivity = if (switchGameMode.isChecked) GravityGameActivity::class.java else NormalGameActivity::class.java
            val intent = Intent(this, targetActivity)
            intent.putExtra("namePlayer1", namePlayer1)
            intent.putExtra("namePlayer2", namePlayer2)
            intent.putExtra("colorPlayer1", colorPlayer1)
            intent.putExtra("colorPlayer2", colorPlayer2)
            startActivity(intent)
        }
    }

}
