package com.example.tenziesapp

interface AppContainer {
    val diceGenerator: DiceGenerator
}

class AppContainerImp : AppContainer {
    override val diceGenerator: DiceGenerator by lazy {
        DiceGeneratorImp()
    }
}
