package com.example.vimechecker.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentAchievementsBinding
import com.example.vimechecker.model.achievement.Achievement
import com.example.vimechecker.model.achievement.Achievements
import com.example.vimechecker.model.achievement.player.PAchievement
import com.example.vimechecker.model.playerOnline.PlayerOnlineItem
import com.example.vimechecker.view.recyclerview.AchievementsAdapter
import com.example.vimechecker.viewmodel.AchivementsViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class AchievementsFragment : Fragment() {
    lateinit var binding: FragmentAchievementsBinding
    lateinit var viewModel: AchivementsViewModel

    private var adapter = AchievementsAdapter()
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("API-Achievements"))
    private val handler = Handler(Looper.getMainLooper())

    private var currentServerAchievements: Achievements? = null
    private lateinit var markedAchievements: List<Achievement>
    private lateinit var defaultAchievements: List<Achievement>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AchivementsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAchievementsBinding.inflate(layoutInflater)
        setupRecyclerView()

        if(arguments?.getBoolean("server-achievements") == true) {
            binding.mainBoxLayout.visibility = View.GONE
            binding.showAllCheckBox.visibility = View.GONE
        }

        arguments!!.getParcelable<PlayerOnlineItem>("player")?.let { fillPlayerLayout(it) }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        if(savedInstanceState == null) getAchievements()

        binding.showAllCheckBox.setOnCheckedChangeListener { _, state ->
            if(state) {
                Log.d("Test", markedAchievements.size.toString())
                adapter = AchievementsAdapter()
                binding.achievementsRcView.adapter = adapter
                adapter.loadAchievements(markedAchievements)
                adapter.notifyDataSetChanged()
            }
            else {
                Log.d("Test", defaultAchievements.size.toString())
                adapter = AchievementsAdapter()
                binding.achievementsRcView.adapter = adapter
                adapter.loadAchievements(defaultAchievements)
                adapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun fillPlayerLayout(player: PlayerOnlineItem) {
        val onlineColor =
            if (!player.online.value) ContextCompat.getColor(requireActivity().applicationContext, R.color.grey)
            else ContextCompat.getColor(requireActivity().applicationContext, R.color.green)

        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val netDate = Date(player.lastSeen.toLong() * 1000)
        binding.lastEnterTextView2.text = sdf.format(netDate)

        binding.nicknameTextView.text = player.username
        binding.levelTextView.text = player.level.toString()
        binding.rankTextView.text = player.rank
        binding.onlineTextView.setTextColor(onlineColor)
        binding.onlineTextView.text = if (player.online.value) "[ONLINE]" else "[OFFLINE]"

        loadAvatar(player.username)
    }

    private fun loadAvatar(username: String) {
        Glide.with(this)
            .load("https://skin.vimeworld.ru/helm/$username.png")
            .centerCrop()
            .placeholder(R.drawable.steve)
            .into(binding.avatarImageView)
    }

    private fun getAchievements() {
        viewModel.getAchievements(true)

        if(arguments?.getBoolean("server-achievements") == false)
            viewModel.getAchievements(false, arguments?.getInt("id-player-achievement"))
    }

    private fun setupObserver() {
        viewModel.serverAchievements.observe(this) { value ->
            if(value.body() == null) return@observe

            val array = achievementsToList(value.body()!!)
            currentServerAchievements = value.body()!!

            if(arguments?.getBoolean("server-achievements") == true) {
                adapter.loadAchievements(sortServerAchievements(array))
                adapter.notifyItemRangeChanged(0, array.size)

                binding.achievementsRcView.setHasFixedSize(true)
            }
        }

        viewModel.playerAchievements.observe(this) { value ->
            if(value.body() == null) return@observe

            scope.launch {
                while(currentServerAchievements == null) {
                    delay(50L)
                }

                markedAchievements = sortServerAchievements(collectAchievements(value.body()!!.achievements, true), true)
                for(i in markedAchievements) Log.d("Achievement1", i.title)
                defaultAchievements = sortServerAchievements(collectAchievements(value.body()!!.achievements))
                for(i in defaultAchievements) Log.d("Achievement2", i.title)
                Log.d("Test", "${defaultAchievements.size}, ${markedAchievements.size}")
                adapter.loadAchievements(markedAchievements)

                handler.post { setupRecyclerView() }
            }
        }
    }

    private fun collectAchievements(pA: List<PAchievement>, mark: Boolean = false): MutableList<Achievement> {
        val sAchievements = achievementsToList(currentServerAchievements!!)
        val pAchievement = pA

        val curwa: MutableList<Achievement> = arrayListOf()
        if(!mark)
            for(achievement in sAchievements) {
                for(pAchiev in pAchievement) {
                    if(achievement.id == pAchiev.id) {
                        curwa.add(achievement)
                    }
                }
            }
        else {
            for(achievement in sAchievements) {
                var found = false
                for(pAchiev in pAchievement) {
                    if(achievement.id == pAchiev.id) {
                        found = true
                    }
                }
                if(!found) achievement.title += "{-1"
                curwa.add(achievement)
            }
        }

        handler.post {
            if(!mark) binding.achievementProgressTextView.text = "Достижения: ${curwa.size}/${sAchievements.size}"
        }

        return curwa
    }

    private fun sortServerAchievements(sAchievement: List<Achievement>, print: Boolean = false): List<Achievement> {
        val list: List<Achievement> = sAchievement

        var lastGame = ""
        for(achievement in list) {
            if(print) Log.d("Title", achievement.title)
            val currentGame = selectGame(achievement.id / 100)
            if(lastGame != currentGame) {
                lastGame = currentGame

                if(print) Log.d("TitleAdd", "Добавил ${achievement.title} - |$lastGame")
                achievement.title += "|$lastGame"
            }
        }

        return list
    }

    private fun selectGame(id: Int): String {
        when(id) {
            0 -> return "global"
            1 -> return "lobby"
            2 -> return "sw"
            3 -> return "gg"
            4 -> return "kpvp"
            5, 50 -> return "dr"
            6 -> return "ann"
            7 -> return "bw"
            8 -> return "mw"
            9 -> return "bp"
            10 -> return "hg"
            12 -> return "bb"
            13 -> return "cp"
            14 -> return "duels"
            15 -> return "prison"
        }

        return ""
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.achievementsRcView.layoutManager = layoutManager
        binding.achievementsRcView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(
            binding.achievementsRcView.context,
            layoutManager.orientation
        )
        binding.achievementsRcView.addItemDecoration(dividerItemDecoration)
    }

    private fun achievementsToList(achievements: Achievements): MutableList<Achievement> {
        val list: MutableList<Achievement> = arrayListOf()

        achievements.Глобальные?.let { list.addAll(it) }
        achievements.Лобби?.let { list.addAll(it) }
        achievements.Annihilation?.let { list.addAll(it) }
        achievements.BedWars?.let { list.addAll(it) }
        achievements.BlockParty?.let { list.addAll(it) }
        achievements.BuildBattle?.let { list.addAll(it) }
        achievements.ClashPoint?.let { list.addAll(it) }
        achievements.Дуэли?.let { list.addAll(it) }
        achievements.DeathRun?.let { list.addAll(it) }
        achievements.GunGame?.let { list.addAll(it) }
        achievements.HungerGames?.let { list.addAll(it) }
        achievements.KitPvP?.let { list.addAll(it) }
        achievements.MobWars?.let { list.addAll(it) }
        achievements.Prison?.let { list.addAll(it) }
        achievements.SkyWars?.let { list.addAll(it) }

        return list
    }
}