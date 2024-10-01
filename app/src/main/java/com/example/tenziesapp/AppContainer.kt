package com.example.tenziesapp

interface AppContainer {
    val diceGenerator: List<Dice>
}

class AppContainerImp : AppContainer {
    override val diceGenerator: List<Dice> by lazy {
        List(10) { Dice() }
    }
}
