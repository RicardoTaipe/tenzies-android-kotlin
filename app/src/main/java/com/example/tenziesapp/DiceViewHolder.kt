package com.example.tenziesapp

import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tenziesapp.databinding.DiceLayoutBinding

class DiceViewHolder(private val binding: DiceLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dice: Dice,
        itemClickListener: ((Dice, position: Int) -> Unit)?,
        isAnimationEnabled: Boolean,
        onEndAnimationListener: ((Boolean) -> Unit)?
    ) {
        binding.root.setOnClickListener {
            itemClickListener?.invoke(dice, adapterPosition)
        }

        binding.diceImg.run {
            setImageResource(retrieveImage(dice.value))
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    if (dice.isSelected) R.color.purple_200 else R.color.white
                ), PorterDuff.Mode.MULTIPLY
            )
            contentDescription = dice.value.toString()
        }

        if (isAnimationEnabled && dice.isUnSelected) {
            ObjectAnimator.ofFloat(binding.root, View.ROTATION, 360f, 0f).apply {
                if (isRunning) cancel()
                duration = 800
                start()
                doOnEnd {
                    onEndAnimationListener?.invoke(false)
                }
            }
        }
        binding.executePendingBindings()
    }

    private fun retrieveImage(value: Int): Int {
        return when (value) {
            1 -> R.drawable.dice1
            2 -> R.drawable.dice2
            3 -> R.drawable.dice3
            4 -> R.drawable.dice4
            5 -> R.drawable.dice5
            6 -> R.drawable.dice6
            else -> R.drawable.dice1 // Default case
        }
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