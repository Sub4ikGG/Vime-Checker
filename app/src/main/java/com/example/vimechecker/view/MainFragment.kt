package com.example.vimechecker.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentMainBinding
import com.example.vimechecker.model.projectOnline.OnlineModel
import com.example.vimechecker.view.recyclerview.OnlineAdapter
import com.example.vimechecker.viewmodel.MainViewModel
import kotlinx.coroutines.*

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private var scope = CoroutineScope(Dispatchers.IO + CoroutineName("API-Main"))

    private var adapter = OnlineAdapter()

    private var isViewInit = false
    private var whatIsOpened = "nothing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setupOnlineChecker()
        setupOnlineObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(layoutInflater)
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isViewInit) progressBar(true)
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.adminsOnlineButton.setOnClickListener {
            if(whatIsOpened != ADMINS_PANEL) {
                whatIsOpened = ADMINS_PANEL
                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout_main, AdminsListFragment())
                    .commit()
            }
        }

        binding.allAchievementsButton.setOnClickListener {
            findNavController().navigate(R.id.teleport_to_achievements, bundleOf("server-achievements" to true))
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        binding.mgOnlineRcView.adapter = adapter
        binding.mgOnlineRcView.layoutManager = layoutManager
    }

    private fun setupOnlineChecker() {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if(this@MainFragment.isVisible) {
                    Log.d("Test", "Отправляю запрос")
                    viewModel.getInfo()
                    delay(15000L)
                }
                else delay(500L)
            }
        }
    }

    private fun setupOnlineObserver() {
        viewModel.onlineLiveData.observe(this) { _it ->
            Log.d("Test", "MainFragment | Осталось запросов: ${_it.headers()["X-RateLimit-Remaining"]}".trim())
            _it.body()?.let {
                updateUI(it)
            }
        }
    }

    private fun updateUI(model: OnlineModel) {

        /*Я пока не узнал, как исправить баг с тем, если подходит лимит запросов,
        то программа крашит :(*/

        if(model.separated != null && model != null) {
            val text = "Всего игроков: ${model.total}"
            binding.allOnlineTextView.text = text
            adapter.setupOnline(sortListPairDesc(model.separated.toList()))
            adapter.notifyItemRangeChanged(0, model.separated.toList().size)

            progressBar(false)
            isViewInit = true

            binding.mgOnlineRcView.setHasFixedSize(true)
        }
    }

    private fun sortListPairDesc(list: List<Pair<String, Int>>): List<Pair<String, Int>> {
        return list.sortedWith(compareBy({ it.second }, { it.first })).asReversed()
    }

    private fun progressBar(state: Boolean) {
        binding.progressBar.visibility = if(state) View.VISIBLE else View.GONE
        binding.mainLayout.visibility = if(state) View.INVISIBLE else View.VISIBLE
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val ADMINS_PANEL = "admins_panel"
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}