package com.oneseed.hangman

import android.content.Context
import android.content.SharedPreferences
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
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.sharedPref), Context.MODE_PRIVATE
        )
        binding.scoreText.text = sharedPref?.getInt(getString(R.string.score), 0).toString()
        binding.playButton.setOnClickListener {
            view.findNavController().navigate(R.id.gameFragment)
        }

        binding.timerSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref?.edit()?.putBoolean(getString(R.string.timerShared), isChecked)?.apply()
            binding.timerText.text = if (isChecked) getString(R.string.timerOn) else getString(R.string.timerOff)
        }
        super.onViewCreated(view, savedInstanceState)

    }


}