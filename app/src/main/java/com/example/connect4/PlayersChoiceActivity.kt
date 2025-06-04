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

class PlayersChoiceActivity : ComponentActivity() {

    private lateinit var namePlayer1: String
    private lateinit var namePlayer2: String
    private lateinit var colorPlayer1: String
    private lateinit var colorPlayer2: String
    private lateinit var gameMode: String

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
    private lateinit var buttonShowDescription: Button
    private lateinit var buttonShowAbout: Button
    private lateinit var popUpDescriptionNormal: RelativeLayout
    private lateinit var popUpDescriptionGravity: RelativeLayout
    private lateinit var popUpAbout: RelativeLayout
    private lateinit var buttonCloseDescription: Button
    private lateinit var buttonCloseAbout: Button
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
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
        buttonShowDescription = findViewById(R.id.btn_show_description)
        buttonShowAbout = findViewById(R.id.btn_show_about)
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

    }

    private fun setStartingVariables(colors: List<String>) {
        namePlayer1 = intent.getStringExtra("namePlayer1") ?: ""
        namePlayer2 = intent.getStringExtra("namePlayer2") ?: ""
        colorPlayer1 = intent.getStringExtra("colorPlayer1") ?: colors[0]
        colorPlayer2 = intent.getStringExtra("colorPlayer2") ?: colors[4]
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
