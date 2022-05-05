package com.example.vimechecker.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentGuildBinding
import com.example.vimechecker.model.guild.GuildModel
import com.example.vimechecker.model.guild.Member
import com.example.vimechecker.room.AppDatabase
import com.example.vimechecker.room.Request
import com.example.vimechecker.room.RequestDao
import com.example.vimechecker.room.RequestViewModel
import com.example.vimechecker.view.recyclerview.GuildMemberAdapter
import com.example.vimechecker.view.recyclerview.GuildMembersInfoAdapter
import com.example.vimechecker.viewmodel.GuildViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class GuildFragment : Fragment() {
    private lateinit var binding: FragmentGuildBinding
    private lateinit var adapter: GuildMemberAdapter
    private lateinit var infoAdapter: GuildMembersInfoAdapter
    private lateinit var viewModel: GuildViewModel
    private lateinit var mRequestViewModel: RequestViewModel
    private lateinit var db: RequestDao

    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Guild"))

    private var requests = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Test", "Guild: onCreate")
        adapter = GuildMemberAdapter(this, findNavController())
        infoAdapter = GuildMembersInfoAdapter(this)
        viewModel = ViewModelProvider(this)[GuildViewModel::class.java]
        mRequestViewModel = ViewModelProvider(this)[RequestViewModel::class.java]
        db = AppDatabase.getDatabase(context!!).requestDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("Test", "Guild: onCreateView")
        binding = FragmentGuildBinding.inflate(layoutInflater)

        progressBar(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null && requests == 0) runLogic()

        setupObserver()
    }

    private fun runLogic() {
        Log.d("Test", "Поиск гильдии..")
        val guild = requireArguments().getString(GUILDNAME, "")

        scope.launch {
            progressBar(true)
            delay(DELAY)
            viewModel.getInfo(guild)
        }
    }

    private fun setupObserver() {
        viewModel.guildLiveData.observe(this) {
            val guild = it.body()
            if(guild != null && guild.members != null) {
                updateUI(guild)
            }
            else {
                binding.progressBar4.visibility = View.GONE
                binding.errorTextView.visibility = View.VISIBLE
                binding.errorImageView.visibility = View.VISIBLE
            }

            if(it.headers()["X-RateLimit-Remaining"]?.toInt() != requests) {
                requests = it.headers()["X-RateLimit-Remaining"]?.toInt()!!
                Log.d("Test", "Guild | Осталось запросов: $requests".trim())
            }

            progressBar(false)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        val layoutManager2 = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.membersRcVIew.adapter = adapter
        binding.membersRcVIew.layoutManager = layoutManager

        binding.membersInfoRcVIew.adapter = infoAdapter
        binding.membersInfoRcVIew.layoutManager = layoutManager2
        binding.membersInfoRcVIew.addItemDecoration(
            DividerItemDecoration(context,
            DividerItemDecoration.VERTICAL)
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateUI(guild: GuildModel) {
        val expText = "Опыт: ${guild.totalExp} exp."
        val coinsText = "Коинов: ${guild.totalCoins} c."

        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val netDate = Date(guild.created.toLong() * 1000)

        binding.dateTextView.text = sdf.format(netDate)
        binding.nameTextView.text = guild.name
        binding.tagTextView.text = guild.tag
        binding.expTextView.text = expText
        binding.coinsTextView.text = coinsText
        binding.levelTextView.text = guild.level.toString()
        if(guild.web_info != null) {
            binding.quoteTextView.text = guild.web_info.toString()
            binding.quoteGuildTextView.text = "@${guild.name}"
        }

        adapter.updateInfo(setLeaderFirst(guild.members))
        scope.launch(Dispatchers.Main) { loadAvatar(guild.avatar_url) }

        setupRecyclerView()
        progressBar(false)

        if(arguments?.getBoolean("search") == true) {
            scope.launch(Dispatchers.IO) {
                addToList(guild)
            }
        }

        setupMembersInfo(guild.members)
        binding.content.visibility = View.VISIBLE
    }

    private fun setupMembersInfo(members: List<Member>) {
        binding.membersInfoBox.visibility = View.VISIBLE
        val m = members.sortedWith(compareBy({ it.guildCoins }, { it.guildExp })).asReversed()
        infoAdapter.updateList(m)
        infoAdapter.notifyItemRangeChanged(0, members.size)
    }

    private suspend fun addToList(guild: GuildModel) {
        val temp = db.getPlayer(guild.name, "Гильдия")
        if (temp == null) {
            mRequestViewModel.addPlayer(
                Request(
                    0,
                    guild.name,
                    guild.id,
                    guild.avatar_url,
                    guild.tag,
                    guild.level,
                    "Гильдия"
                )
            )
        }
        else {
            mRequestViewModel.upPlayer(temp,  Request(
                0,
                guild.name,
                guild.id,
                guild.avatar_url,
                guild.tag,
                guild.level,
                "Гильдия"
            ))
        }
    }

    private fun setLeaderFirst(guild: List<Member>): List<Member> {
        val newGuild = guild.toMutableList()
        if(newGuild.size == 1) return newGuild

        var indexOfLeader = 0
        for(i in newGuild) {
            if(i.status == "LEADER") indexOfLeader = newGuild.indexOf(i)
        }

        val temp = newGuild[indexOfLeader]
        newGuild[indexOfLeader] = newGuild[0]
        newGuild[0] = temp

        return newGuild
    }

    private fun loadAvatar(url: String) {
        Glide.with(this)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.steve)
            .into(binding.avatarImageView)
    }

    private fun progressBar(state: Boolean) {
        binding.progressBar4.visibility = if(state) View.VISIBLE else View.GONE
    }

    companion object {
        private const val GUILDNAME = "guildname"
        private const val DELAY = 300L

        @JvmStatic
        fun newInstance(guildName: String) = GuildFragment().apply {
            arguments = bundleOf(GUILDNAME to guildName)
        }
    }
}