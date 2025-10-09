package com.example.tenziesapp

import androidx.annotation.VisibleForTesting

object ServiceLocator {
    @Volatile
    var gameManager: GameManager? = null
        @VisibleForTesting set

    fun provideGameManager(): GameManager {
        return gameManager ?: synchronized(this) {
            gameManager ?: createRealGameManager().also { gameManager = it }
        }
    }

    private fun createRealGameManager(): GameManager {
        return TenziesGameManager(createDiceGenerator())
    }

    private fun createDiceGenerator(): DiceGenerator {
        return DiceGeneratorImp()
    }
}