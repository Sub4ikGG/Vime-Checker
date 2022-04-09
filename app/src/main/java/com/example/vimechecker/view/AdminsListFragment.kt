package com.example.vimechecker.view

import android.os.Bundle
import android.util.Log
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
    private lateinit var binding: FragmentAdminsListBinding
    private lateinit var adapter: AdminsAdapter
    private lateinit var viewModel: AdminsListViewModel

    private var scope = CoroutineScope(Dispatchers.IO + CoroutineName("API-Admins"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AdminsListViewModel::class.java]
        adapter = AdminsAdapter(this, findNavController())
        if(savedInstanceState == null) runLogic()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAdminsListBinding.inflate(layoutInflater)
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar3.visibility = View.GONE
        setupObserver()
    }

    private fun runLogic() {
        scope.launch {
            Log.d("Test", "Admins: запуск логики")
            viewModel.getAdmins()
        }
    }

    private fun setupObserver() {
        viewModel.adminsLiveData.observe(this) {
            Log.d("Test", "Admins: ${it.body()?.size}")
            if(it.body()?.isNotEmpty() == true) {
                adapter.setupList(it.body()!!)
                adapter.notifyItemRangeChanged(0, it.body()!!.size)

                binding.adminsListRcView.setHasFixedSize(true)
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.adminsListRcView.layoutManager = layoutManager
        binding.adminsListRcView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdminsListFragment()
    }
}