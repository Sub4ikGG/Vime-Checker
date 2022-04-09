package com.example.vimechecker.view.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vimechecker.R
import com.example.vimechecker.retrofit.Transcriptions
import com.example.vimechecker.databinding.OnlineItemBinding

class OnlineAdapter: RecyclerView.Adapter<OnlineAdapter.OnlineViewHolder>() {
    private var onlineList = emptyList<Pair<String, Int>>()

    class OnlineViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = OnlineItemBinding.bind(view)
        fun bind(pair: Pair<String, Int>, position: Int) {
            val textPlayers = "Игроков: ${pair.second}"

            binding.gameNameTextView.text = Transcriptions.localeNames[pair.first]
            binding.gamePlayerTextView.text = textPlayers
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.online_item, parent, false)
        return OnlineViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnlineViewHolder, position: Int) {
        holder.bind(onlineList[position], position)
    }

    override fun getItemCount(): Int {
        return onlineList.size
    }

    fun setupOnline(list: List<Pair<String, Int>>) {
        onlineList = list
    }
}