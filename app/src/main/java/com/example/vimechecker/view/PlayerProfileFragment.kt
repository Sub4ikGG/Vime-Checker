package com.example.vimechecker.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentPlayerProfileBinding
import com.example.vimechecker.model.lastgame.LastGamesModel
import com.example.vimechecker.model.playerFriends.Friend
import com.example.vimechecker.model.playerFriends.PlayerFriends
import com.example.vimechecker.model.playerOnline.PlayerOnline
import com.example.vimechecker.view.recyclerview.FriendsAdapter
import com.example.vimechecker.view.recyclerview.LastGamesAdapter
import com.example.vimechecker.viewmodel.PlayerProfileViewModel
import com.example.vimechecker.viewmodel.PlayerProfileViewModelFactory
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class PlayerProfileFragment : Fragment() {
    lateinit var binding: FragmentPlayerProfileBinding
    lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: PlayerProfileViewModel
    private var scope = CoroutineScope(Dispatchers.IO + CoroutineName("API-Player"))
    //private var handler = Handler(Looper.getMainLooper())
    private lateinit var friendsAdapter: FriendsAdapter
    private var lastGamesAdapter = LastGamesAdapter()
    private var guildName = ""

    private var requests = 0
    private var viewCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //val viewModelFactory = PlayerProfileViewModelFactory()
        viewModel = ViewModelProvider(this)[PlayerProfileViewModel::class.java]
        friendsAdapter = FriendsAdapter(this, findNavController())
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        runLogic()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerProfileBinding.inflate(layoutInflater)

        showProgressBar()
        setupClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewCreated = true
    }

    private fun runLogic() {
        scope.launch(Dispatchers.IO) {
            val nick = arguments?.getString(NICKNAME, "")
            Log.d("Test", "Запуск логики $nick ${this.isActive}")
            if (nick != null && nick != "") {
                if (!viewModel.getPlayerInfo(nick)) {
                    scope.launch(Dispatchers.Main) {
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.progressBar2.visibility = View.GONE
                    }
                }
            } else if (sharedPref.getString("TOKEN-NICKNAME", "") != "") {
                viewModel.getPlayerInfo(sharedPref.getString("TOKEN-NICKNAME", "")!!)
            } else {
                scope.launch(Dispatchers.Main) {
                    binding.progressBar2.visibility = View.GONE
                    showTokenLayout(true)
                }
            }
        }
    }

    private fun showTokenLayout(bool: Boolean) {
        binding.mainLayout.visibility = if(bool) View.GONE else View.VISIBLE
        binding.tokenLayout.visibility = if(bool) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners() {
        binding.apply {
            logoImageViewC.setOnClickListener {
                if(tokenInputEditText.text.toString().isNotEmpty()) {
                    scope.launch {
                        scope.launch(Dispatchers.Main) {  binding.progressBar2.visibility = View.VISIBLE }
                        viewModel.checkToken(tokenInputEditText.text.toString())
                    }
                }
            }

            fullStatButton.setOnClickListener {
                //findNavController().navigate(R.id.guildFragment)
            }

            guildButton.setOnClickListener {
                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout_playerProfile, GuildFragment.newInstance(guildName))
                    .commit()
            }
        }
    }

    private fun setupObservers() {
        //Log.d("Test", "SetupObservers")
        viewModel.playerLiveData.observe(this) { it ->
            it.body()?.let {
                //Log.d("Test", it.toString())
                updateUI(it)
            }
        }

        viewModel.playerFriends.observe(this) {
            //Log.d("Test", it.toString())
            friendsAdapter.updateInfo(it)
        }

        viewModel.tokenInfo.observe(this) { it ->
            it?.body()?.let {
                Log.d("Test", it.toString())
                if (it.valid && it.type == "AUTH") {
                    println("Token success!")
                    with(sharedPref.edit()) {
                        putString("TOKEN-NICKNAME", it.owner.username)
                        apply()
                    }
                    arguments?.putString(NICKNAME, it.owner.username)
                    binding.tokenLayout.visibility = View.GONE
                    runLogic()
                }
                else {
                    println("Token unsuccess!")
                    Toast.makeText(context, "Токен неактивен", Toast.LENGTH_SHORT).show()
                }

                binding.progressBar2.visibility = View.GONE
            }
        }

        viewModel.lastGamesLiveData.observe(this) { it ->
            //Log.d("Test", it.toString())
            if(it.headers()["X-RateLimit-Remaining"]?.toInt() != requests) {
                requests = it.headers()["X-RateLimit-Remaining"]?.toInt()!!
                Log.d("Test", "Осталось запросов: ${it.headers()["X-RateLimit-Remaining"]}".trim())
            }
            it?.body()?.let {
                it.matches?.let { it1 -> lastGamesAdapter.updateInfo(it1) }
                setupRecyclerView()
                hideProgressBar()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateUI(m: PlayerOnline) {
        val model = m[0]
        val onlineColor = if(!model.online.value) ContextCompat.getColor(requireActivity().applicationContext, R.color.grey)
        else ContextCompat.getColor(requireActivity().applicationContext, R.color.green)

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(model.lastSeen.toLong() * 1000)

        binding.nicknameTextView.text = model.username
        binding.levelTextView.text = model.level.toString()
        binding.rankTextView.text = model.rank
        binding.statusTextView.text = if(model.online.value) model.online.message else "Игрок оффлайн"
        binding.onlineTextView.text = if(model.online.value) "[ONLINE]" else "[OFFLINE]"
        binding.onlineTextView.setTextColor(onlineColor)

        if(model.guild == null) {
            binding.guildButton.isEnabled = false
            binding.guildButton.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.grey))
        }
        else {
            guildName = model.guild.name
        }

        loadAvatar(model.username)
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

    private fun showProgressBar() {
        binding.mainLayout.visibility = View.INVISIBLE
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        scope.launch(Dispatchers.Main) {
            delay(100)
            binding.mainLayout.visibility = View.VISIBLE
            binding.progressBar2.visibility = View.GONE
        }
    }

    private fun Toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val NICKNAME = "nickname"

        @JvmStatic
        fun newInstance(nickname: String = "") = PlayerProfileFragment().apply {
            arguments = bundleOf(NICKNAME to nickname)
        }
    }
}