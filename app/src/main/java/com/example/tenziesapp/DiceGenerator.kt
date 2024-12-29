package com.example.tenziesapp

interface DiceGenerator {
    fun generateNewDice(): List<Dice>
    fun generateSingleDice(): Dice
}

class DiceGeneratorImp : DiceGenerator {
    override fun generateNewDice() = List(10) { Dice() }
    override fun generateSingleDice() = Dice()
}
