package com.example.vimechecker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vimechecker.databinding.FragmentGuildBinding
import com.example.vimechecker.view.recyclerview.GuildMemberAdapter
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class GuildFragment : Fragment() {
    lateinit var binding: FragmentGuildBinding
    private lateinit var adapter: GuildMemberAdapter
    private lateinit var viewModel: GuildViewModel
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Guild"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = GuildMemberAdapter(this)
        viewModel = ViewModelProvider(this)[GuildViewModel::class.java]

        setupObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentGuildBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        runLogic(requireArguments().getString(GUILDNAME, ""))
    }

    private fun runLogic(guild: String) {
        scope.launch {
            try {
                viewModel.getInfo(guild)
            }
            catch(e: Exception) {
                println(e.message)
            }
        }
    }

    private fun setupObservers() {
        viewModel.guildLiveData.observe(this) {
            println(it.body())
            it.body().let { guild ->
                binding.nameTextView.text = guild?.name
                binding.tagTextView.text = guild?.tag
                //adapter.updateInfo(it2!!.members)
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)
        binding.membersRcVIew.adapter = adapter
        binding.membersRcVIew.layoutManager = layoutManager
    }

    companion object {
        private const val GUILDNAME = "guildname"

        @JvmStatic
        fun newInstance(guildName: String) = GuildFragment().apply {
            arguments = bundleOf(GUILDNAME to guildName)
        }
    }
}