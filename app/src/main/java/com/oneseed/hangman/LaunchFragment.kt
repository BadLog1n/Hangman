package com.oneseed.hangman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.oneseed.hangman.databinding.FragmentLaunchBinding


class LaunchFragment: Fragment() {
    private lateinit var binding: FragmentLaunchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLaunchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playButton.setOnClickListener {
            view.findNavController().navigate(R.id.gameFragment)
        }
    }


}