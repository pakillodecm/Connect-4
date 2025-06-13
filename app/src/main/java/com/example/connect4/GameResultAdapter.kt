package com.example.connect4

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connect4.database.GameResult

class GameResultAdapter(private var results: List<GameResult>): RecyclerView.Adapter<GameResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtPlayers: TextView = view.findViewById(R.id.txt_players)
        val txtScore: TextView = view.findViewById(R.id.txt_score)
        val txtWinner: TextView = view.findViewById(R.id.txt_winner)
        val txtMode: TextView = view.findViewById(R.id.txt_mode)
        val txtDate: TextView = view.findViewById(R.id.txt_date)
        val txtDuration: TextView = view.findViewById(R.id.txt_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_game_result_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = results.size

    private fun formatLabel(label: String, value: String): SpannableStringBuilder {
        return SpannableStringBuilder().apply {
            val boldSpan = android.text.style.StyleSpan(android.graphics.Typeface.BOLD)
            append(label, boldSpan, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            append(value)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.txtPlayers.text = formatLabel("Players: ", "${result.player1} vs ${result.player2}")
        holder.txtScore.text = formatLabel("Score: ", result.score)
        holder.txtWinner.text = formatLabel("Winner: ", result.winner)
        holder.txtMode.text = formatLabel("Mode: ", result.mode)
        holder.txtDate.text = formatLabel("Date: ", result.date)
        holder.txtDuration.text = formatLabel("Duration: ", result.duration)
    }

    fun updateData(newResults: List<GameResult>) {
        results = newResults
        notifyDataSetChanged()
    }

}
