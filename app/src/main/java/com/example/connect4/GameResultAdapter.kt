package com.example.connect4

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
        val txtDate: TextView = view.findViewById(R.id.txt_date)
        val txtMode: TextView = view.findViewById(R.id.txt_mode)
        val txtWinner: TextView = view.findViewById(R.id.txt_winner)
        val txtDuration: TextView = view.findViewById(R.id.txt_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_game_result_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.txtPlayers.text = "Players: ${result.namePlayer1} vs ${result.namePlayer2}"
        holder.txtScore.text = "Score: ${result.score}"
        holder.txtDate.text = "Date: ${result.date}"
        holder.txtMode.text = "Mode: ${result.mode}"
        holder.txtWinner.text = "Winner: ${result.winner}"
        holder.txtDuration.text = "Duration: ${result.duration}"
    }

    fun updateData(newResults: List<GameResult>) {
        results = newResults
        notifyDataSetChanged()
    }
}
