package com.example.vimechecker.view.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vimechecker.R
import com.example.vimechecker.retrofit.Transcriptions
import com.example.vimechecker.databinding.LastgameItemBinding
import com.example.vimechecker.model.lastgame.Matche

class LastGamesAdapter: RecyclerView.Adapter<LastGamesAdapter.LastGamesHolder>() {
    private var lastGamesList = emptyList<Matche>()

    class LastGamesHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = LastgameItemBinding.bind(view)
        fun bind(game: Matche) {
            binding.lastGameTextView.text = Transcriptions.localeNames[game.game.lowercase()]
            binding.lastGameStatusTextView.text = if(game.win) "Победа" else "Проигрыш"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastGamesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lastgame_item, parent, false)
        return LastGamesHolder(view)
    }

    override fun onBindViewHolder(holder: LastGamesHolder, position: Int) {
        holder.bind(lastGamesList[position])
    }

    override fun getItemCount(): Int {
        return lastGamesList.size
    }

    fun updateInfo(list: List<Matche>) {
        if(list != lastGamesList) {
            lastGamesList = list
        }
    }
}