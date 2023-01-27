package com.example.tenziesapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


class DiceViewModel : ViewModel() {
    private var dice = emptyList<Dice>()
    private val _diceUi = MutableLiveData<List<Dice>>()
    val diceUi: LiveData<List<Dice>>
        get() = _diceUi

    var gameOver: LiveData<Boolean> = Transformations.map(_diceUi) {
        val allHeld = dice.all { it.isSelected }
        val firstValue = dice[0].value
        val allSameValue = dice.all { it.value == firstValue }
        allHeld && allSameValue
    }

    init {
        dice = generateNewDice()
        _diceUi.value = dice
    }

    private fun generateNewDice(): List<Dice> {
        return List(10) {
            Dice()
        }
    }

    fun rollDice() {
        dice = if (gameOver.value == false) {
            dice.map { item ->
                if (item.isSelected) item else Dice()
            }
        } else {
            generateNewDice()
        }
        _diceUi.value = dice
    }

    fun holdDice(id: String) {
        if (gameOver.value == true) return
        dice = dice.map { item ->
            if (item.id == id) {
                item.isSelected = !item.isSelected
                item.rolling = false
            }
            item
        }
        _diceUi.value = dice
    }
}