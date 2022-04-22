package com.example.vimechecker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vimechecker.R
import com.example.vimechecker.databinding.FragmentAboutProjectBinding

class AboutProjectFragment : Fragment() {
    lateinit var binding: FragmentAboutProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAboutProjectBinding.inflate(layoutInflater)
        return binding.root
    }
}