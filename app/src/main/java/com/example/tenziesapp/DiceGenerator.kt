package com.example.tenziesapp

interface DiceGenerator {
    fun generateNewDice(): List<Dice>
}

class DiceGeneratorImp : DiceGenerator {
    override fun generateNewDice(): List<Dice> {
        return List(10) { Dice() }
    }
}
