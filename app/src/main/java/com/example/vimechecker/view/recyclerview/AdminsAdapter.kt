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
import com.example.vimechecker.model.admin.AdminModel
import com.example.vimechecker.model.admin.AdminModelItem

class AdminsAdapter(private val fragment: Fragment, val navController: NavController): RecyclerView.Adapter<AdminsAdapter.AdminsHolder>() {
    private var adminsList = AdminModel()

    class AdminsHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = LitePlayerItemBinding.bind(view)
        fun bind(model: AdminModelItem, fragment: Fragment, navController: NavController) {
            binding.nicknameTextView.text = model.username
            binding.levelTextView.text = model.level.toString()
            binding.rankTextView.text = model.rank
            binding.statusTextView.text = "Находится онлайн"

            Glide.with(fragment)
                .load("https://skin.vimeworld.ru/helm/${model.username}.png")
                .skipMemoryCache(false)
                .centerCrop()
                .placeholder(R.drawable.steve)
                .into(binding.avatarImageView)

            binding.avatarImageView.setOnClickListener {
                navController.navigate(R.id.playerProfileFragment2, bundleOf("nickname" to model.username))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lite_player_item, parent, false)
        return AdminsHolder(view)
    }

    override fun onBindViewHolder(holder: AdminsHolder, position: Int) {
        holder.bind(adminsList[position], fragment, navController)
    }

    override fun getItemCount(): Int {
        return adminsList.size
    }

    fun setupList(model: AdminModel) {
        adminsList = model
    }
}