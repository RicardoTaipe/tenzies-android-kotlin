package com.example.tenziesapp

class TenziesGame {
    var dice: List<Dice> = emptyList()


    init {
        dice = generateNewDice()
    }

    fun generateNewDice(): List<Dice> {
        return List(10) {
            Dice()
        }
    }

    private fun areAllDiceSelected() = dice.all { it.isSelected }

    fun isGameOver(): Boolean {
        val allHeld = areAllDiceSelected()
        val firstValue = dice.first().value
        val allSameValue = dice.all { it.value == firstValue }
        return allHeld && allSameValue
    }

    fun rollDice(): List<Dice> {
        dice = if (!isGameOver()) {
            dice.map { item ->
                if (item.isSelected) item else Dice()
            }
        } else {
            generateNewDice()
        }
        return dice
    }

    fun holdDice(id: String) {
        if (!isGameOver()) {
            dice.find { it.id == id }?.let {
                it.isSelected = !it.isSelected
            }
        }
    }
}