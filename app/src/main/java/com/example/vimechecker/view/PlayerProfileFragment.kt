package com.example.vimechecker.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentPlayerProfileBinding
import com.example.vimechecker.model.playerOnline.PlayerOnline
import com.example.vimechecker.model.playerOnline.PlayerOnlineItem
import com.example.vimechecker.room.AppDatabase
import com.example.vimechecker.room.Request
import com.example.vimechecker.room.RequestDao
import com.example.vimechecker.room.RequestViewModel
import com.example.vimechecker.view.recyclerview.FriendsAdapter
import com.example.vimechecker.view.recyclerview.LastGamesAdapter
import com.example.vimechecker.viewmodel.PlayerProfileViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class PlayerProfileFragment : Fragment() {
    private lateinit var binding: FragmentPlayerProfileBinding
    private lateinit var viewModel: PlayerProfileViewModel
    private lateinit var mRequestViewModel: RequestViewModel
    private lateinit var sharedPref: SharedPreferences
    private lateinit var db: RequestDao
    private lateinit var currentPlayer: PlayerOnlineItem

    private lateinit var friendsAdapter: FriendsAdapter
    private var lastGamesAdapter = LastGamesAdapter()

    private var scope = CoroutineScope(Dispatchers.IO + CoroutineName("API-Player"))
    private var handler = Handler(Looper.getMainLooper())

    private lateinit var guildName: String
    private var currentId: Int = 0
    private var requests = 0
    private var timerStarted = false
    private var timerSecondsLeft = 0
    private var guildOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //val viewModelFactory = PlayerProfileViewModelFactory()
        viewModel = ViewModelProvider(this)[PlayerProfileViewModel::class.java]
        mRequestViewModel = ViewModelProvider(this)[RequestViewModel::class.java]
        friendsAdapter = FriendsAdapter(this, findNavController())
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        db = AppDatabase.getDatabase(context!!).requestDao()

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerProfileBinding.inflate(layoutInflater)
        checkStatement(savedInstanceState)
        setupRecyclerView()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
        setupClickListeners()
    }

    private fun checkStatement(savedInstanceState: Bundle?) {
        val nickname = arguments?.getString(NICKNAME, null)
        val tokenNickname = sharedPref.getString("TOKEN-NICKNAME", null)

        if (savedInstanceState == null && requests == 0) {
            scope.launch { runLogic(selectNotNull(nickname, tokenNickname)) }
        } else {
            if (selectNotNull(nickname, tokenNickname) == null) showTokenLayout()
            if (binding.nicknameTextView.text == "TeTTJIo" && binding.nicknameTextView.visibility == View.VISIBLE
                && binding.tokenLayout.visibility == View.GONE) {
                showErrorMessage()
            }
        }

        if (savedInstanceState?.getInt("timerSec") != 0) savedInstanceState?.getInt("timerSec")?.let { runTimer(it) }

        if (timerStarted) {
            binding.updateButton.isEnabled = false
            binding.updateButton.setTextColor(
                ContextCompat.getColor(
                    requireActivity().applicationContext,
                    R.color.grey
                )
            )
        }
    }

    private suspend fun runLogic(target: String?, timer: Boolean = true) {
        if (target != null) {
            handler.post { progressBar(true) }; delay(DELAY)
            Log.d("Test", "Поиск информации о $target..")
            if (target.contains(" ")) handler.post { showErrorMessage() }
            else if (!viewModel.getPlayerInfo(target)) handler.post { showErrorMessage() }

            if (timer) handler.post { runTimer() }
        } else {
            showTokenLayout()
        }
    }

    private fun showErrorMessage() {
        binding.mainLayout.visibility = View.GONE
        binding.progressBar2.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorImageView.visibility = View.VISIBLE
    }

    private fun selectNotNull(a: String?, b: String?): String? {
        return a ?: b
    }

    private fun showTokenLayout(state: Boolean = true) {
        binding.mainLayout.visibility = if (state) View.GONE else View.INVISIBLE
        binding.tokenLayout.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners() {
        binding.apply {
            logoImageViewC.setOnClickListener {
                if (tokenInputEditText.text.toString().isNotEmpty()) {
                    scope.launch {
                        scope.launch(Dispatchers.Main) { binding.progressBar2.visibility = View.VISIBLE }
                        viewModel.checkToken(tokenInputEditText.text.toString())
                    }
                }
                else {
                    Toast.makeText(context, "/api auth", Toast.LENGTH_LONG).show()
                }
            }

            fullStatButton.setOnClickListener {
                //findNavController().navigate(R.id.guildFragment)
            }

            achievementsButton.setOnClickListener {
                Log.d("Test", "$currentPlayer")
                findNavController().navigate(
                    R.id.teleport_to_achievements,
                    bundleOf(
                        "server-achievement" to false, "id-player-achievement" to currentPlayer.id,
                        "player" to currentPlayer
                    )
                )
            }

            guildButton.setOnClickListener {
                if (!guildOpened) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_playerProfile, GuildFragment.newInstance(guildName))
                        .commit()

                    guildOpened = true
                }
            }

            updateButton.setOnClickListener {
                val nickname = arguments?.getString(NICKNAME, null)
                val tokenNickname = sharedPref.getString("TOKEN-NICKNAME", null)
                scope.launch { runLogic(selectNotNull(nickname, tokenNickname), false) }
                Toast.makeText(context, "Обновление данных..", Toast.LENGTH_SHORT).show()

                runTimer()
            }
        }
    }

    private fun runTimer(sec: Int = 15) {
        timerStarted = true
        binding.updateButton.isEnabled = false
        binding.updateButton.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.grey))

        var text = "До следующего обновления: $sec сек."
        binding.updateButton.text = text

        scope.launch {
            timerSecondsLeft = sec
            while (timerSecondsLeft > 0) {
                if (this@PlayerProfileFragment.isVisible) {
                    text = "До следующего обновления: $timerSecondsLeft сек."
                    binding.updateButton.text = text
                    timerSecondsLeft--

                    /*Log.d("Test", "Таймер: осталось $i сек.")*/
                }
                if (this@PlayerProfileFragment.isVisible) delay(1000L) else delay(100L)
            }

            timerStarted = false
            if (this@PlayerProfileFragment.isVisible) {
                scope.launch(Dispatchers.Main) {
                    binding.updateButton.isEnabled = true
                    binding.updateButton.text = "Обновить"
                    binding.updateButton.setTextColor(
                        ContextCompat.getColor(
                            requireActivity().applicationContext,
                            R.color.white
                        )
                    )
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.playerLiveData.observe(this) { it ->
            it.body()?.let {
                updateUI(it)
            }
        }

        viewModel.playerFriends.observe(this) {
            friendsAdapter.updateInfo(it)
            friendsAdapter.notifyItemRangeChanged(0, it.size)

            binding.friendsRcView.setHasFixedSize(true)
        }

        viewModel.tokenInfo.observe(this) { it ->
            it?.body()?.let {
                if (it.valid && it.type == "AUTH" && binding.nicknameTextView.text != it.owner.username) {
                    with(sharedPref.edit()) {
                        putString("TOKEN-NICKNAME", it.owner.username)
                        apply()
                    }

                    binding.tokenLayout.visibility = View.GONE
                    scope.launch { runLogic(it.owner.username) }
                }
            }

            binding.progressBar2.visibility = View.GONE
        }

        viewModel.lastGamesLiveData.observe(this) { it ->
            if (it.headers()["X-RateLimit-Remaining"]?.toInt() != requests) {
                requests = it.headers()["X-RateLimit-Remaining"]?.toInt()!!
                Log.d("Test", "PPFragment | Осталось запросов: $requests".trim())
            }

            it?.body()?.let {
                it.matches.let { it1 -> lastGamesAdapter.updateInfo(it1) }
                lastGamesAdapter.notifyItemRangeChanged(0, it.matches.size)
                binding.lastGamesRcView.setHasFixedSize(true)

                progressBar(false)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateUI(m: PlayerOnline) {
        val model = m[0]; currentPlayer = model
        val onlineColor =
            if (!model.online.value) ContextCompat.getColor(requireActivity().applicationContext, R.color.grey)
            else ContextCompat.getColor(requireActivity().applicationContext, R.color.green)

        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val netDate = Date(model.lastSeen.toLong() * 1000)

        binding.lastEnterTextView.text = sdf.format(netDate)

        binding.nicknameTextView.text = model.username
        binding.levelTextView.text = model.level.toString()
        binding.rankTextView.text = model.rank
        binding.statusTextView.text = if (model.online.value) model.online.message else "Игрок оффлайн"
        binding.onlineTextView.text = if (model.online.value) "[ONLINE]" else "[OFFLINE]"
        binding.onlineTextView.setTextColor(onlineColor)

        if (model.guild == null) {
            binding.guildButton.isEnabled = false
            binding.guildButton.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.grey))
        } else {
            guildName = model.guild.name
        }

        loadAvatar(model.username)

        if (arguments?.getBoolean("search", false) == true) {
            scope.launch(Dispatchers.IO) {
                addToList(model)
            }
        }

        currentId = model.id
    }

    private fun addToList(item: PlayerOnlineItem) {
        val temp = db.getPlayer(item.username, "Игрок")
        if (temp == null) {
            Log.d("Test", "Добавил ${item.username} в БД")
            mRequestViewModel.addPlayer(
                Request(
                    0,
                    item.username,
                    item.id,
                    "https://skin.vimeworld.ru/helm/${item.username}.png",
                    item.rank,
                    item.level,
                    "Игрок"
                )
            )
        } else {
            mRequestViewModel.upPlayer(
                temp, Request(
                    0,
                    item.username,
                    item.id,
                    "https://skin.vimeworld.ru/helm/${item.username}.png",
                    item.rank,
                    item.level,
                    "Игрок"
                )
            )
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, 2)
        binding.friendsRcView.adapter = friendsAdapter
        binding.friendsRcView.layoutManager = layoutManager

        val layoutManager2 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.lastGamesRcView.adapter = lastGamesAdapter
        binding.lastGamesRcView.layoutManager = layoutManager2
    }

    private fun loadAvatar(username: String) {
        Glide.with(this)
            .load("https://skin.vimeworld.ru/helm/$username.png")
            .centerCrop()
            .placeholder(R.drawable.steve)
            .into(binding.avatarImageView)
    }

    private fun progressBar(state: Boolean) {
        binding.progressBar2.visibility = if (state) View.VISIBLE else View.GONE
        binding.mainLayout.visibility = if (state) View.INVISIBLE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("timerSec", timerSecondsLeft)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        private const val NICKNAME = "nickname"
        private const val DELAY = 300L
    }
}