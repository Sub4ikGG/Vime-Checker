package com.example.vimechecker.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
    private var scope = CoroutineScope(Dispatchers.IO + CoroutineName("API"))

    private var adapter = OnlineAdapter()
    private var checkerStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Test", "MainFragment - onCreate")

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setupOnlineObserver()
        setupOnlineChecker()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Test", "MainFragment - onCreateView")
        binding = FragmentMainBinding.inflate(layoutInflater)
        binding.progressBar.visibility = View.VISIBLE
        binding.mainLayout.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Test", "MainFragment - onViewCreated")

        if(checkerStarted) {
            binding.progressBar.visibility = View.GONE
            binding.mainLayout.visibility = View.VISIBLE
        }

        setupRecyclerView()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.adminsOnlineButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.frameLayout_main, AdminsListFragment())
                .commit()
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
                viewModel.getInfo()
                delay(15000L)
            }
        }
    }

    private fun setupOnlineObserver() {
        viewModel.onlineLiveData.observe(this) { _it ->
            _it.body()?.let {
                checkerStarted = true
                Log.d("Test", "Data update")
                updateUI(it)
            }
        }
    }

    private fun updateUI(model: OnlineModel) {
        val text = "Всего игроков: ${model.total}"
        binding.allOnlineTextView.text = text
        adapter.setupOnline(sortListPairDesc(model.separated.toList()))

        binding.mainLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun sortListPairDesc(list: List<Pair<String, Int>>): List<Pair<String, Int>> {
        return list.sortedWith(compareBy({ it.second }, { it.first })).asReversed()
    }

    private fun Toast(text: String) {
        android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}