package com.oneseed.hangman

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.hangman.databinding.FragmentGameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TIMER_COUNT = 30

class GameFragment : Fragment(), LettersAdapter.RecyclerViewEvent {
    private val lettersList = ArrayList<LettersItem>()
    private val rcAdapter = LettersAdapter(lettersList, this)
    private lateinit var wordAsked: String
    private var trying = 0
    private var hintCount = 0
    private var timerCount = TIMER_COUNT
    private lateinit var sharedPref: SharedPreferences
    private var sharedScore = 0
    private var score = 1000
    private lateinit var arrayOfAnswers: CharArray
    private lateinit var binding: FragmentGameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref =
            activity?.getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)!!
        sharedPref.getInt(getString(R.string.scoreShared), 0).also { sharedScore = it }
        hintCount = sharedPref.getInt(getString(R.string.hintShared), 0)
        sharedPref.edit().putInt(getString(R.string.scoreShared), 0).apply()
        binding.hintCount.text = hintCount.toString()

        binding.inputCode.isEnabled = false
        val imageRc: RecyclerView = view.findViewById(R.id.abcButtonsRecycler)
        imageRc.adapter = rcAdapter
        for (i in '??'..'??') {
            rcAdapter.addLetter(
                LettersItem(i.toString())
            )
        }
        rcAdapter.notifyItemChanged(rcAdapter.itemCount)
        wordAsked = requireArguments().getString("wordAsked")!!
        binding.inputCode.lengthOfCode = wordAsked.length
        imageRc.layoutManager = GridLayoutManager(context, 5)
        val localArray = CharArray(wordAsked.length) { ' ' }
        arrayOfAnswers = localArray

        if (requireArguments().getBoolean("timer")) {
            score = 3000
            binding.timer.visibility = View.VISIBLE
            binding.timer.text = timerCount.toString()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    while (timerCount > 0) {
                        Thread.sleep(1000)
                        timerCount--
                        withContext(Dispatchers.Main) {
                            if (timerCount > 0) binding.timer.text = timerCount.toString()
                            if (timerCount == 0) {
                                AlertDialog.Builder(context).setTitle("???? ??????????????????!")
                                    .setMessage("?????????? ??????????!\n???????????????????? ??????????: \"${wordAsked.lowercase()}\".")
                                    .setCancelable(false).setPositiveButton("OK") { _, _ ->
                                        findNavController().navigateUp()
                                    }.show()
                                timerCount = -1
                            }
                        }
                    }
                }
            }
        }



        binding.hintImage.setOnClickListener {
            if (hintCount > 0) {
                hintCount--
                binding.hintCount.text = hintCount.toString()
                sharedPref.edit().putInt(getString(R.string.hintShared), hintCount).apply()
                var randomLetter = wordAsked.random()
                while (arrayOfAnswers.contains(randomLetter)) {
                    randomLetter = wordAsked.random()
                }
                isRightAnswer(randomLetter)
                rcAdapter.disableLetter(randomLetter)


            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(context).setTitle("???? ??????????????, ?????? ???????????? ?????????? ???? ?????????")
                .setMessage("???????? ?????????? ?????????????????? ??????????????????????!").setPositiveButton("????") { _, _ ->
                    findNavController().navigateUp()
                }.setNegativeButton("??????") { _, _ -> }.show()
        }


        super.onViewCreated(view, savedInstanceState)
    }


    override fun onItemClicked(letter: Char) {
        if (wordAsked.contains(letter)) {
            isRightAnswer(letter)
        } else {
            isWrongAnswer()
        }
        rcAdapter.disableLetter(letter)

    }


    private fun isRightAnswer(letter: Char) {
        for (i in wordAsked.indices) {
            if (wordAsked[i] == letter) {
                arrayOfAnswers[i] = letter
            }
        }
        binding.inputCode.code = String(arrayOfAnswers)
        if (String(arrayOfAnswers) == wordAsked) {
            AlertDialog.Builder(context).setTitle("??????????????????????!")
                .setMessage("???? ?????????????? ?????????? \"${wordAsked.lowercase()}\"!").setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    findNavController().navigateUp()
                }.show()
            trying++
            sharedPref.edit()
                .putInt(getString(R.string.scoreShared), sharedScore + (score / trying)).apply()
            sharedPref.edit().putInt(getString(R.string.hintShared), ++hintCount).apply()


            timerCount = -1

        }
    }

    @SuppressLint("DiscouragedApi")
    private fun isWrongAnswer() {
        trying++
        if (trying == 6) {
            AlertDialog.Builder(context).setTitle("???? ??????????????????!")
                .setMessage("???? ???? ?????????????? ??????????!\n???????????????????? ??????????: \"${wordAsked.lowercase()}\".")
                .setCancelable(false).setPositiveButton("OK") { _, _ ->
                    findNavController().navigateUp()
                }.show()
            timerCount = -1

        }
        if (trying < 7) {
            val image = "@drawable/hangman_$trying"
            val resID = resources.getIdentifier(image, "drawable", activity?.packageName)
            binding.imageView.setImageResource(resID)
        }
    }


}