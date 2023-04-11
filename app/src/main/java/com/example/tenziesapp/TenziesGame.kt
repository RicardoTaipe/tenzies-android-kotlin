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

    fun isGameOver(): Boolean {
        val allHeld = dice.all { it.isSelected }
        val firstValue = dice[0].value
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
        if (isGameOver()) return
        dice = dice.map { item ->
            if (item.id == id) {
                item.isSelected = !item.isSelected
                item.rolling = false
            }
            item
        }
    }
}