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
import com.example.vimechecker.model.guild.GuildModel
import com.example.vimechecker.model.guild.Member
import com.example.vimechecker.view.recyclerview.GuildMemberAdapter
import com.example.vimechecker.viewmodel.GuildViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GuildFragment : Fragment() {
    lateinit var binding: FragmentGuildBinding
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Guild"))
    private lateinit var adapter: GuildMemberAdapter
    private lateinit var viewModel: GuildViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = GuildMemberAdapter(this)
        viewModel = ViewModelProvider(this)[GuildViewModel::class.java]

        setupObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuildBinding.inflate(layoutInflater)

        binding.content.visibility = View.INVISIBLE
        binding.progressBar4.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runLogic()
    }

    private fun runLogic() {
        val guild = requireArguments().getString(GUILDNAME, "")
        scope.launch {
            viewModel.getInfo(guild)
        }
    }

    private fun setupObserver() {
        viewModel.guildLiveData.observe(this) {
            if(it.body() != null) {
                updateUI(it.body()!!)
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.membersRcVIew.adapter = adapter
        binding.membersRcVIew.layoutManager = layoutManager

        binding.content.visibility = View.VISIBLE
        binding.progressBar4.visibility = View.GONE
    }

    private fun updateUI(guild: GuildModel) {
        binding.nameTextView.text = guild.name
        binding.tagTextView.text = guild.tag
        binding.levelTextView.text = guild.level.toString()

        adapter.updateInfo(setLeaderFirst(guild.members))
        setupRecyclerView()
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

    companion object {
        private const val GUILDNAME = "guildname"

        @JvmStatic
        fun newInstance(guildName: String) = GuildFragment().apply {
            arguments = bundleOf(GUILDNAME to guildName)
        }
    }
}