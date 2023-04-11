package com.example.tenziesapp

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class TenziesGameTest {
    private lateinit var game: TenziesGame

    @Before
    fun init() {
        game = TenziesGame()
    }

    @Test
    fun generateNewDice() {
        assertEquals(game.generateNewDice().size, 10)
    }

    @Test
    fun whenRollDiceShouldAllDiceBeUnselected() {
        val expected = game.rollDice()
        assertTrue(expected.all { dice -> !dice.isSelected })
    }

    @Test
    fun whenADiceIsHeldShouldNotChangeValue() {
        val diceList = List(10) {
            Dice()
        }
        game.dice = diceList
        val diceToHeld = diceList[0]
        game.holdDice(diceToHeld.id)
        val expected = game.rollDice().first { it.id == diceToHeld.id }
        assertTrue(expected.isSelected)
        assertEquals(expected.value, diceToHeld.value)
    }

    @Test
    fun whenDiceIsNotSelectedGenerateNewDice() {
        val diceList = List(10) {
            Dice()
        }
        game.dice = diceList
        game.rollDice()
        val expected = game.dice.map { it.value }
        assertNotEquals(expected, diceList.map { it.value })
    }

    @Test
    fun whenAllDiceHaveSameValueGameIsOver() {
        val diceList = List(10) {
            Dice(value = 3)
        }
        game.dice = diceList
        diceList.forEach {
            game.holdDice(it.id)
        }
        assertTrue(game.isGameOver())
    }

}