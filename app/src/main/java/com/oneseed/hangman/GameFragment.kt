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


class GameFragment : Fragment(), LettersAdapter.RecyclerViewEvent {
    private val lettersList = ArrayList<LettersItem>()
    private val rcAdapter = LettersAdapter(lettersList, this)
    private lateinit var inputString: String
    private var trying = 0
    private var timerCount = 30
    private var hintCount = 30
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
        for (i in 'А'..'Я') {
            rcAdapter.addLetter(
                LettersItem(i.toString())
            )
        }
        rcAdapter.notifyItemChanged(rcAdapter.itemCount)

        inputString = (getString(R.string.words)).split(" ").random().replace("ё", "е").uppercase()
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
                                    .setMessage("Время вышло!\nПравильный ответ: \"${inputString.lowercase()}\".")
                                    .setCancelable(false).setPositiveButton("OK") { _, _ ->
                                        findNavController().navigateUp()
                                    }.show()

                            }
                            timerCount = -1
                        }
                    }
                }
            }
        }



        binding.hintImage.setOnClickListener{
            if (hintCount > 0) {
                hintCount--
                binding.hintCount.text = hintCount.toString()
                sharedPref.edit().putInt(getString(R.string.hintShared), hintCount).apply()

                //random letter from inputString and not in arrayOfAnswers
                var randomLetter = inputString.random()
                while (arrayOfAnswers.contains(randomLetter)) {
                    randomLetter = inputString.random()
                }
                isRightAnswer(randomLetter)
                rcAdapter.disableLetter(randomLetter)


            }
        }








        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(context).setTitle("Вы уверены, что хотите выйти из игры?")
                .setMessage("Игра будет считаться проигранной!").setPositiveButton("Да") { _, _ ->
                    findNavController().navigateUp()
                }.setNegativeButton("Нет") { _, _ -> }.show()
        }


        super.onViewCreated(view, savedInstanceState)
    }


    override fun onItemClicked(letter: Char) {
        if (inputString.contains(letter)) {
            isRightAnswer(letter)
        } else {
            isWrongAnswer()
        }
        rcAdapter.disableLetter(letter)

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
                .setMessage("Вы угадали слово \"${inputString.lowercase()}\"!").setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    findNavController().navigateUp()
                }.show()
            trying++
            sharedPref.edit().putInt(getString(R.string.scoreShared), score / trying).apply()
            sharedPref.edit().putInt(getString(R.string.hintShared), ++hintCount).apply()


            timerCount = -1

        }
    }

    @SuppressLint("DiscouragedApi")
    private fun isWrongAnswer() {
        trying++
        if (trying == 6) {
            AlertDialog.Builder(context).setTitle("Вы проиграли!")
                .setMessage("Вы не угадали слово!\nПравильный ответ: \"${inputString.lowercase()}\".")
                .setCancelable(false).setPositiveButton("OK") { _, _ ->
                    findNavController().navigateUp()
                }.show()
            timerCount = -1

        }
        val image = "@drawable/hangman_$trying"
        val resID = resources.getIdentifier(image, "drawable", activity?.packageName)
        binding.imageView.setImageResource(resID)

    }


}