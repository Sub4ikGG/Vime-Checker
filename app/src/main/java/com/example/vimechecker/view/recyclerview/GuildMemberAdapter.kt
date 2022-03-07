package com.example.vimechecker.view.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FriendItemBinding
import com.example.vimechecker.model.guild.Member
import com.example.vimechecker.model.playerFriends.Friend

class GuildMemberAdapter(private val fragment: Fragment): RecyclerView.Adapter<GuildMemberAdapter.MemberHolder>() {
    private var memberList = emptyList<Member>()

    class MemberHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = FriendItemBinding.bind(view)
        fun bind(member: Member, fragment: Fragment) {
            Glide.with(fragment)
                .load("https://skin.vimeworld.ru/helm/${member.user.username}.png")
                .centerCrop()
                .placeholder(R.drawable.steve)
                .into(binding.avatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return MemberHolder(view)
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.bind(memberList[position], fragment)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateInfo(list: List<Member>) {
        memberList = list
        notifyDataSetChanged()
    }
}