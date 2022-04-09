package com.example.vimechecker.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentFindPlayerBinding
import com.example.vimechecker.view.recyclerview.LastRequestsAdapter
import kotlinx.coroutines.*

class FindSomethingFragment : Fragment() {
    private lateinit var binding: FragmentFindPlayerBinding

    private var savedInstState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstState = savedInstanceState
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindPlayerBinding.inflate(layoutInflater)
        savedInstState = savedInstanceState
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(savedInstState == null) {
            Log.d("Test", "FindSomething: onResume")
            childFragmentManager.beginTransaction()
                .replace(R.id.last_requests_frameLayout, LastRequestsFragment.newInstance(findNavController())).commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListener()
        setupObserver()
    }

    private fun setupObserver() {
        /*mRequestViewModel.readAllData.observe(this) {
            if(it.isNotEmpty()) binding.lastRequestsTextView.visibility = View.VISIBLE

            adapter.loadRequests(it.reversed())
            progressBar(false)
        }*/
    }

    private fun setupClickListener() {
        binding.findPlayerButton.setOnClickListener {
            val nickname = binding.objectInputEditText.text.toString()
            if(nickname.isNotEmpty()) {
                hideKeyboard()
                findNavController().navigate(R.id.teleport_to_player, bundleOf("nickname" to nickname, "search" to true))
            }
        }

        binding.findGuildButton.setOnClickListener {
            val guild = binding.objectInputEditText.text.toString()
            if(guild.isNotEmpty()) {
                hideKeyboard()

                //viewModel.setCurrentObject(guild)
                //childFragmentManager.beginTransaction().replace(R.id.frameLayout_findSomething, GuildFragment.newInstance(guild)).commit()
                findNavController().navigate(R.id.teleport_to_guild, bundleOf("guildname" to guild, "search" to true))
            }
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
        view?.clearFocus()
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FindSomethingFragment()
    }
}