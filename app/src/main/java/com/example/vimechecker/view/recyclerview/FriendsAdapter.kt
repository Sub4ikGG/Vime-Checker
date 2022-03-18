package com.example.vimechecker.view.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FriendItemBinding
import com.example.vimechecker.model.playerFriends.Friend

class FriendsAdapter(private val fragment: Fragment, val navController: NavController): RecyclerView.Adapter<FriendsAdapter.FriendsHolder>() {
    var friends = mutableListOf<Friend>()

    class FriendsHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = FriendItemBinding.bind(view)
        fun bind(friend: Friend, fragment: Fragment, navController: NavController) {
            if(friend.id != -1) {
                Glide.with(fragment)
                    .load("https://skin.vimeworld.ru/helm/${friend.username}.png")
                    .centerCrop()
                    .placeholder(R.drawable.steve)
                    .into(binding.avatar)
            }
            else binding.avatar.setImageResource(R.drawable.anonymous)

            binding.avatar.setOnClickListener {
                if(friend.id != -1) {
                    println(navController.graph.id)
                    try {
                        navController.navigate(
                            R.id.action_findSomethingFragment2_to_playerProfileFragment,
                            bundleOf("nickname" to friend.username)
                        )
                    }
                    catch (e: Exception) {
                        navController.navigate(
                            R.id.action_playerProfileFragment4_self,
                            bundleOf("nickname" to friend.username)
                        )
                    }
                }
            }

            binding.avatar.setOnLongClickListener {
                if(friend.id != -1) {
                    Toast.makeText(fragment.context, friend.username, Toast.LENGTH_SHORT).show()
                    true
                }
                else false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return FriendsHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsHolder, position: Int) {
        holder.bind(friends[position], fragment, navController)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun updateInfo(f: MutableList<Friend>) {
        if(f != friends) {
            friends = f
        }
    }
}