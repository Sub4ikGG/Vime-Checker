package com.example.vimechecker.view.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.GuildPlayerInfoBinding
import com.example.vimechecker.model.guild.Member

class GuildMembersInfoAdapter(private val fragment: Fragment): RecyclerView.Adapter<GuildMembersInfoAdapter.MembersInfoViewHolder>() {
    private var membersList = emptyList<Member>()

    class MembersInfoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = GuildPlayerInfoBinding.bind(view)
        fun bind(member: Member, fragment: Fragment) {
            binding.nicknameTextView.text = member.user.username
            binding.levelTextView.text = member.user.level.toString()
            binding.rankTextView.text = member.user.rank

            binding.donateCoinsTextView.text = "1. Coins: ${member.guildCoins} coins."
            binding.donateExpTextView.text = "2. Exp: ${member.guildExp} exp."

            Glide.with(fragment)
                .load("https://skin.vimeworld.ru/helm/${member.user.username}.png")
                .centerCrop()
                .placeholder(R.drawable.steve)
                .into(binding.avatarImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guild_player_info, parent, false)
        return MembersInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MembersInfoViewHolder, position: Int) {
        holder.bind(membersList[position], fragment)
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    fun updateList(list: List<Member>) {
        membersList = list
    }
}