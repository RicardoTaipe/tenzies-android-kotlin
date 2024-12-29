package com.example.tenziesapp

import org.jetbrains.annotations.VisibleForTesting

object ServiceLocator {

    @Volatile
    var diceGenerator: DiceGenerator = DiceGeneratorImp()
        @VisibleForTesting set
    fun provideDiceGenerator(): DiceGenerator = diceGenerator
}