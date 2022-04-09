package com.example.vimechecker.view.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.LitePlayerItemBinding
import com.example.vimechecker.room.Request

class LastRequestsAdapter(val fragment: Fragment, private val navController: NavController): RecyclerView.Adapter<LastRequestsAdapter.LastRequestsViewHolder>() {
    private var requestsList = emptyList<Request>()

    class LastRequestsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = LitePlayerItemBinding.bind(view)
        fun bind(request: Request, fragment: Fragment, navController: NavController) {
            val toGuildText = "Перейти к гильдии"
            val toProfileText = "Перейти к профилю"

            binding.nicknameTextView.text = request.name
            binding.rankTextView.text = request.rankTag
            binding.levelTextView.text = request.level.toString()
            binding.onlineTextView.text = ""

            binding.toProfileTextViewC.text = if(request.type == "Игрок") toProfileText else toGuildText

            Glide.with(fragment)
                .load(request.avatar)
                .skipMemoryCache(false)
                .centerCrop()
                .placeholder(R.drawable.steve)
                .into(binding.avatarImageView)

            binding.toProfileTextViewC.setOnClickListener {
                val path = if(request.type == "Игрок") R.id.teleport_to_player else R.id.teleport_to_guild
                val pairString = if(request.type == "Игрок") "nickname" else "guildname"

                navController.navigate(path, bundleOf(pairString to request.name, "search" to true))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastRequestsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lite_player_item, parent, false)
        return LastRequestsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LastRequestsViewHolder, position: Int) {
        holder.bind(requestsList[position], fragment, navController)
    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

    fun loadRequests(list: List<Request>) {
        requestsList = list
    }
}