package com.example.tenziesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.tenziesapp.databinding.DiceLayoutBinding

class DiceAdapter : ListAdapter<Dice, DiceViewHolder>(DiceDiffCallback) {
    var itemClickListener: ((Dice, Int) -> Unit)? = null
    var isAnimationEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiceViewHolder {
        val binding = DiceLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiceViewHolder, position: Int) {
        holder.bind(
            getItem(position), itemClickListener, isAnimationEnabled
        ) {
            isAnimationEnabled = it
        }
    }
}

