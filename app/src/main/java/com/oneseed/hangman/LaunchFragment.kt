package com.oneseed.hangman

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.oneseed.hangman.databinding.FragmentLaunchBinding


class LaunchFragment : Fragment() {
    private lateinit var binding: FragmentLaunchBinding
    private val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/badlog1n"))

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
        val score = sharedPref?.getInt(getString(R.string.scoreShared), 0)!!.toInt()
        binding.scoreText.text = score.toString()
        val record = sharedPref.getInt(getString(R.string.recordShared), 0)
        if (score > record) {
            sharedPref.edit().putInt(getString(R.string.recordShared), score).apply()
            binding.recordText.text = score.toString()
        } else {
            binding.recordText.text = record.toString()
        }

        binding.authorText.setOnClickListener{
            startActivity(intent)
        }

        binding.playButton.setOnClickListener {
            val wordAsked = (getString(R.string.words)).split(" ").random().replace("ё", "е")
                .uppercase()
            while (wordAsked.length > 12) {
                wordAsked + (getString(R.string.words)).split(" ").random().replace("ё", "е")
                    .uppercase()
            }

            val bundle = Bundle()
            bundle.putBoolean("timer", binding.timerSwitch.isChecked)
            bundle.putString(
                "wordAsked",
                wordAsked
            )
            view.findNavController().navigate(R.id.gameFragment, bundle)
        }
        binding.timerSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.timerText.text =
                if (isChecked) getString(R.string.timerOn) else getString(R.string.timerOff)
        }
        super.onViewCreated(view, savedInstanceState)

    }


}