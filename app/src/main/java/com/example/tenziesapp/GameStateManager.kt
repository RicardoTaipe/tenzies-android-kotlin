package com.example.tenziesapp

interface GameManager {
    /** The current list of dice being played. */
    val currentDiceState: List<Dice>

    /** True if the game is over (all dice are locked and have matching values). */
    val isGameOver: Boolean

    /** Rolls all unlocked dice and updates the dice state. */
    fun rollDice()

    /** Toggles the lock status for a specific die. */
    fun lockDice(diceId: String)

    /** Resets the game by generating a new set of unlocked dice. */
    fun restartGame()
}

class TenziesGameManager(
    private val diceGenerator: DiceGenerator,
) : GameManager {

    private var _tenziesDice: List<Dice> = diceGenerator.generateNewDice()

    override val currentDiceState: List<Dice>
        get() = _tenziesDice

    override val isGameOver: Boolean
        get() = areAllDiceMatching() && areAllDiceLocked()

    override fun rollDice() {
        if (isGameOver) {
            restartGame()
        } else {
            updateDiceAfterRoll()
        }
    }

    override fun restartGame() {
        _tenziesDice = diceGenerator.generateNewDice()
    }

    override fun lockDice(diceId: String) {
        if (!isGameOver) {
            toggleDiceLockStatus(diceId)
        }
    }

    private fun toggleDiceLockStatus(diceId: String) {
        updateDice { dice ->
            if (dice.id == diceId) dice.copy(isSelected = !dice.isSelected) else dice
        }
    }

    private fun updateDiceAfterRoll() {
        updateDice { dice ->
            if (dice.isSelected) dice else diceGenerator.generateSingleDice()
        }
    }

    private fun updateDice(updateFn: (Dice) -> Dice) {
        _tenziesDice = _tenziesDice.map(updateFn)
    }

    private fun areAllDiceLocked() = _tenziesDice.all { it.isSelected }

    private fun areAllDiceMatching(): Boolean = _tenziesDice.run {
        if (isEmpty()) return false
        val firstValue = first().value
        all { it.value == firstValue }
    }
}