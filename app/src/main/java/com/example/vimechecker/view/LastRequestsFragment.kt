package com.example.vimechecker.view

import android.os.Bundle
import android.transition.Transition
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vimechecker.databinding.FragmentLastRequestsBinding
import com.example.vimechecker.room.RequestViewModel
import com.example.vimechecker.view.recyclerview.LastRequestsAdapter
import kotlinx.coroutines.*

class LastRequestsFragment() : Fragment() {
    lateinit var binding: FragmentLastRequestsBinding
    private lateinit var mRequestViewModel: RequestViewModel
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("LRFrag"))

    private lateinit var adapter: LastRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Test", "LastRequests: onCreate")
        adapter = LastRequestsAdapter(this, findNavController())
        mRequestViewModel = ViewModelProvider(this)[RequestViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLastRequestsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        Log.d("Test", "LastRequests: OnResume")
        progressBar(true)
        setupRecyclerView()
        setupObserver()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.requestsRcView.layoutManager = layoutManager
        binding.requestsRcView.adapter = adapter
    }

    private fun setupObserver() {
        mRequestViewModel.readAllData.observe(this) {
            scope.launch(Dispatchers.Main) {
                delay(500L)
                adapter.loadRequests(it.reversed())
                adapter.notifyItemRangeChanged(0, it.size)

                progressBar(false)
                binding.requestsRcView.setHasFixedSize(true)
            }
        }
    }

    private fun progressBar(state: Boolean) {
        binding.progressBar6.visibility = if (state) View.VISIBLE else View.GONE
        binding.requestsRcView.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(navController: NavController) = LastRequestsFragment()
    }
}