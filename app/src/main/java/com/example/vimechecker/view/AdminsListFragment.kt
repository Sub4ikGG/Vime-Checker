package com.example.vimechecker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vimechecker.databinding.FragmentAdminsListBinding
import com.example.vimechecker.view.recyclerview.AdminsAdapter
import com.example.vimechecker.viewmodel.AdminsListViewModel
import kotlinx.coroutines.*
import okhttp3.internal.notifyAll

class AdminsListFragment : Fragment() {
    lateinit var binding: FragmentAdminsListBinding
    private lateinit var adapter: AdminsAdapter
    private var scope = CoroutineScope(Dispatchers.IO + CoroutineName("API-ADMINS"))
    private lateinit var viewModel: AdminsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AdminsListViewModel::class.java]
        setupObserver()
        runLogic()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdminsAdapter(this, findNavController())
        setupRecyclerView()
        binding.progressBar3.visibility = View.GONE
    }

    private fun runLogic() {
        scope.launch {
            viewModel.getAdmins()
        }
    }

    private fun setupObserver() {
        viewModel.adminsLiveData.observe(this, Observer {
            if(it.body()?.isNotEmpty() == true) {
                adapter.setupList(it.body()!!)
                adapter.notifyItemRangeChanged(0, it.body()!!.size)
                println(adapter.itemCount)
            }
        })
    }

    private fun setupRecyclerView() {
        binding.adminsListRcView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.adminsListRcView.adapter = adapter
    }

    override fun onResume() {
        runLogic()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdminsListFragment()
    }
}