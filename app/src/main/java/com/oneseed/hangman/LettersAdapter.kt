package com.oneseed.hangman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.hangman.databinding.RussianAbcButtonBinding

class LettersAdapter(
    private val lettersList: ArrayList<LettersItem>,
    private val listener: RecyclerViewEvent
) :
    RecyclerView.Adapter<LettersAdapter.LettersHolder>() {

    class LettersHolder(item: View, listener: RecyclerViewEvent) : RecyclerView.ViewHolder(item), View.OnClickListener {
        private val binding = RussianAbcButtonBinding.bind(item)
        private val localListener = listener

        fun bind(letterItem: LettersItem) {
            binding.tvTitle.text = letterItem.letter




        }

        init {
            binding.tvTitle.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {
            val letter = binding.tvTitle
            localListener.onItemClicked(letter.text.toString()[0])
            letter.visibility = View.INVISIBLE

        }

    }

    fun addLetter(letterItem: LettersItem) {
        lettersList.add(letterItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LettersHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.russian_abc_button, parent, false)

        return LettersHolder(view, listener)
    }

    override fun onBindViewHolder(holder: LettersHolder, position: Int) {
        holder.bind(lettersList[position])
    }

    override fun getItemCount(): Int {
        return lettersList.size
    }

    interface RecyclerViewEvent {
        fun onItemClicked(letter: Char)
    }


}

data class LettersItem(
    val letter: String,
)
