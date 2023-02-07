package com.oneseed.hangman

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.hangman.databinding.FragmentGameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GameFragment : Fragment(), LettersAdapter.RecyclerViewEvent {
    private val lettersList = ArrayList<LettersItem>()
    private val rcAdapter = LettersAdapter(lettersList, this)
    private lateinit var inputString: String
    private var trying = 0
    private var timerCount = 30
    private lateinit var sharedPref: SharedPreferences
    private var score = 1000
    private lateinit var arrayOfAnswers: CharArray
    private lateinit var binding: FragmentGameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = activity?.getSharedPreferences(
            getString(R.string.sharedPref), Context.MODE_PRIVATE
        )!!

        binding.inputCode.isEnabled = false
        val imageRc: RecyclerView = view.findViewById(R.id.abcButtonsRecycler)
        imageRc.adapter = rcAdapter
        for (i in 'А'..'Я') {
            rcAdapter.addLetter(
                LettersItem(i.toString())
            )
        }
        rcAdapter.notifyItemChanged(rcAdapter.itemCount)

        inputString = (getString(R.string.words)).split(" ").random().uppercase()
        binding.inputCode.lengthOfCode = inputString.length

        imageRc.layoutManager = GridLayoutManager(context, 5)

        val localArray = CharArray(inputString.length) { ' ' }
        arrayOfAnswers = localArray

        if (requireArguments().getBoolean("timer")) {
            score = 3000
            binding.timer.visibility = View.VISIBLE
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    while (timerCount > 0) {
                        binding.timer.text = timerCount.toString()
                        Thread.sleep(1000)
                        timerCount--
                        if (timerCount == 0) {
                            binding.timer.text = timerCount.toString()
                            withContext(Dispatchers.Main) {
                                AlertDialog.Builder(context).setTitle("Вы проиграли!")
                                    .setMessage("Время вышло!\nПравильный ответ: ${inputString.lowercase()}.")
                                    .setCancelable(false).setPositiveButton("OK") { _, _ ->
                                        findNavController().navigateUp()
                                    }.show()
                                sharedPref.edit().putInt(getString(R.string.scoreShared), 0)
                                    .apply()

                            }
                            timerCount = -1
                        }
                    }
                }
            }
        }



        super.onViewCreated(view, savedInstanceState)
    }


    override fun onItemClicked(letter: Char) {
        if (inputString.contains(letter)) {
            isRightAnswer(letter)
        } else {
            isWrongAnswer()
        }

    }


    private fun isRightAnswer(letter: Char) {
        for (i in inputString.indices) {
            if (inputString[i] == letter) {
                arrayOfAnswers[i] = letter
            }
        }
        binding.inputCode.code = String(arrayOfAnswers)
        if (String(arrayOfAnswers) == inputString) {
            AlertDialog.Builder(context).setTitle("Поздравляем!")
                .setMessage("Вы угадали слово ${inputString.lowercase()}!").setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    findNavController().navigateUp()
                }.show()
            trying++
            sharedPref.edit().putInt(getString(R.string.scoreShared), score / trying).apply()


            timerCount = -1

        }
    }

    @SuppressLint("DiscouragedApi")
    private fun isWrongAnswer() {
        trying++
        if (trying == 6) {
            AlertDialog.Builder(context).setTitle("Вы проиграли!")
                .setMessage("Вы не угадали слово!\nПравильный ответ: ${inputString.lowercase()}.")
                .setCancelable(false).setPositiveButton("OK") { _, _ ->
                    findNavController().navigateUp()
                }.show()
            sharedPref.edit().putInt(getString(R.string.scoreShared), 0).apply()

            timerCount = -1

        } else {
            val image = "@drawable/hangman_$trying"
            val resID = resources.getIdentifier(image, "drawable", activity?.packageName)
            binding.imageView.setImageResource(resID)
        }
    }


}