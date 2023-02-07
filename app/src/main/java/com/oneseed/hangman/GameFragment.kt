package com.oneseed.hangman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.hangman.databinding.FragmentGameBinding


class GameFragment : Fragment(), LettersAdapter.RecyclerViewEvent {
    private val lettersList = ArrayList<LettersItem>()
    private val rcAdapter = LettersAdapter(lettersList, this)
    private val inputString = "тест".uppercase()
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
        binding.inputCode.isEnabled = false
        val imageRc: RecyclerView = view.findViewById(R.id.abcButtonsRecycler)
        imageRc.adapter = rcAdapter
        for (i in 'А'..'Я') {
            rcAdapter.addLetter(
                LettersItem(i.toString())
            )
        }

        val localArray = CharArray(inputString.length) { ' ' }
        arrayOfAnswers = localArray

        rcAdapter.notifyItemChanged(rcAdapter.itemCount)

        imageRc.layoutManager = GridLayoutManager(context, 5)

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onItemClicked(letter: Char) {
        for (i in inputString.indices) {
            if (inputString[i] == letter) {
                arrayOfAnswers[i] = letter
            }
        }
        binding.inputCode.code = String(arrayOfAnswers)
    }


}