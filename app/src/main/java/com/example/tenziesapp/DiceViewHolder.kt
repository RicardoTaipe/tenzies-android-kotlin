package com.example.tenziesapp

import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tenziesapp.databinding.DiceLayoutBinding

class DiceViewHolder(private val binding: DiceLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dice: Dice, itemClickListener: ((Dice, position: Int) -> Unit)?
    ) {
        binding.root.setOnClickListener {
            itemClickListener?.invoke(dice, adapterPosition)
        }

        binding.diceImg.run {
            setImageResource(retrieveImage(dice.value))
            setColorFilter(
                ContextCompat.getColor(
                    context, if (dice.isSelected) R.color.purple_200 else R.color.white
                ), PorterDuff.Mode.MULTIPLY
            )

        }
        if (dice.rolling) {
            ObjectAnimator.ofFloat(binding.root, View.ROTATION, 360f, 0f).apply {
                duration = 600
                start()
            }
        }

        binding.executePendingBindings()
    }

    private fun retrieveImage(index: Int): Int {
        return listOf(
            R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
        )[index]
    }

}

object DiceDiffCallback : DiffUtil.ItemCallback<Dice>() {
    override fun areItemsTheSame(oldItem: Dice, newItem: Dice): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Dice, newItem: Dice): Boolean {
        return oldItem == newItem
    }

}