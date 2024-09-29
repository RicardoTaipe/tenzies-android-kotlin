package com.example.tenziesapp

interface DiceGenerator {
    fun generate(): List<Dice>
    fun generateSingleDie(): Dice
}

class DiceGeneratorImp : DiceGenerator {
    override fun generate() = List(10) { Dice() }
    override fun generateSingleDie() = Dice()
}
