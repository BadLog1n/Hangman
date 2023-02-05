package com.oneseed.hangman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.hangman.databinding.RussianAbcButtonBinding

class LettersAdapter : RecyclerView.Adapter<LettersAdapter.LettersHolder>() {
    var lettersList = ArrayList<LettersItem>()

    class LettersHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = RussianAbcButtonBinding.bind(item)

        fun bind(letterItem: LettersItem) {
            binding.tvTitle.text = letterItem.letter

            binding.tvTitle.setOnClickListener{
                binding.tvTitle.visibility = View.INVISIBLE
                binding.tvTitle.text = ""
                binding.tvTitle.isEnabled = false
            }

        }

    }

    fun addLetter(letterItem: LettersItem) {
        lettersList.add(letterItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LettersHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.russian_abc_button, parent, false)

        return LettersHolder(view)
    }

    override fun onBindViewHolder(holder: LettersHolder, position: Int) {
        holder.bind(lettersList[position])
    }

    override fun getItemCount(): Int {
        return lettersList.size
    }
}

data class LettersItem(
    val letter: String,
)
