package com.example.tenziesapp

object GameUtils {
    // Helper for creating Dice objects
    fun createDice(id: String, value: Int, isLocked: Boolean = false) =
        Dice(id = id, value = value, isSelected = isLocked)

    /** A standard 10-dice set representing the initial state of the game.
     * Each dice has a unique id and a value from 1 to 6.
     * Not all dice have the same value, so this is not a winning state.
     */
    val initialNonWinningDice = listOf(
        createDice("d1", 1),
        createDice("d2", 2),
        createDice("d3", 3),
        createDice("d4", 4),
        createDice("d5", 5),
        createDice("d6", 6),
        createDice("d7", 1),
        createDice("d8", 2),
        createDice("d9", 3),
        createDice("d10", 4)
    )

    /**
     * Represents a winning state in the game:
     * All 10 dice have the same value (6) and are locked.
     * Used for testing win condition logic.
     */
    val winningDiceLocked = listOf(
        createDice("d1", 6, true),
        createDice("d2", 6, true),
        createDice("d3", 6, true),
        createDice("d4", 6, true),
        createDice("d5", 6, true),
        createDice("d6", 6, true),
        createDice("d7", 6, true),
        createDice("d8", 6, true),
        createDice("d9", 6, true),
        createDice("d10", 6, true)
    )

    /**
     * A 10-dice set where all dice have the same value (5), but not all are locked.
     * This is used to test scenarios where the win condition is not met because
     * at least one dice (d5) is not locked.
     */
    val matchingDiceNotLocked = listOf(
        createDice("d1", 5, true),
        createDice("d2", 5, true),
        createDice("d3", 5, true),
        createDice("d4", 5, true),
        createDice("d5", 5, false), // d5 is NOT locked
        createDice("d6", 5, true),
        createDice("d7", 5, true),
        createDice("d8", 5, true),
        createDice("d9", 5, true),
        createDice("d10", 5, true)
    )
}