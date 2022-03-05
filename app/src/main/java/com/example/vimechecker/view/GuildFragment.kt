package com.example.vimechecker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentGuildBinding
import com.example.vimechecker.view.recyclerview.GuildMemberAdapter

class GuildFragment : Fragment() {
    lateinit var binding: FragmentGuildBinding
    private var adapter = GuildMemberAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)
        binding.membersRcVIew.adapter = adapter
        binding.membersRcVIew.layoutManager = layoutManager
    }

    companion object {
        @JvmStatic
        fun newInstance() = GuildFragment()
    }
}