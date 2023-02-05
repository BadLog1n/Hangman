package com.oneseed.hangman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.hangman.databinding.FragmentGameBinding
import com.oneseed.hangman.databinding.FragmentLaunchBinding


class GameFragment : Fragment() {
    private val rcAdapter = LettersAdapter()
    private lateinit var binding: FragmentGameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val imageRc: RecyclerView = view.findViewById(R.id.abcButtonsRecycler)
        imageRc.adapter = rcAdapter
        rcAdapter.lettersList = ArrayList()
        for (i in 'А'..'Я'){
            rcAdapter.addLetter(
                LettersItem(i.toString())
            )
        }


        rcAdapter.notifyItemChanged(rcAdapter.itemCount)

        imageRc.layoutManager = GridLayoutManager(context, 5)

        super.onViewCreated(view, savedInstanceState)
    }


}