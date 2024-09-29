package com.example.tenziesapp


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DiceViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DiceViewModel
    private fun getRandomList(
        isGameOver: Boolean = false,
        allSelected: Boolean = false,
        someSelected: Boolean = false,
    ): List<Dice> {
        return when {
            allSelected -> List(10) { Dice(isSelected = true) }
            someSelected -> List(5) { Dice(value = 5, isSelected = true) } + List(5) { Dice() }
            isGameOver -> List(10) { Dice(value = 5, isSelected = true) }
            else -> List(10) { Dice() }
        }
    }

    @Test
    fun whenInitGameThenGetARandomDiceList() {
        val mockDiceList = getRandomList()
        viewModel = DiceViewModel(mockDiceList)

        val actual = viewModel.diceUi.getOrAwaitValue()

        assertEquals(10, actual.size)
        assertEquals(mockDiceList, actual)
    }

    @Test
    fun givenRandomDiceListWhenADiceIsHeldThenDiceShouldBeSelected() {
        val mockDiceList = getRandomList()
        viewModel = DiceViewModel(mockDiceList)

        val diceToHeld = mockDiceList[0]
        viewModel.holdDice(diceToHeld.id)
        val expected = viewModel.diceUi.getOrAwaitValue().first { it.id == diceToHeld.id }

        assertTrue(expected.isSelected)
        assertEquals(expected.value, diceToHeld.value)
    }

    @Test
    fun givenGameIsOverWhenADiceIsSelectedShouldNotSelectAnyDice() {
        val mockDiceList = getRandomList(isGameOver = true)
        viewModel = DiceViewModel(mockDiceList)

        viewModel.holdDice(mockDiceList.first().id)

        val expected = viewModel.diceUi.getOrAwaitValue()
        assertEquals(expected, mockDiceList)
    }

    @Test
    fun givenNoDiceSelectedWhenRollDiceThenGetNewRandomDiceAndPlayRollSound() {
        val mockDiceList = getRandomList()
        viewModel = DiceViewModel(mockDiceList)

        viewModel.rollDice()

        assertNotEquals(mockDiceList, viewModel.diceUi.getOrAwaitValue())
        assertEquals(
            R.raw.rollingdice,
            viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled()
        )
    }

    @Test
    fun givenSomeDiceAreSelectedWhenRollDiceThenNewDiceAreGeneratedAndSelectedShouldNotChange() {
        val mockDiceList = getRandomList(someSelected = true)
        viewModel = DiceViewModel(mockDiceList)

        viewModel.rollDice()

        val (selectedDice, newGeneratedDice) = viewModel.diceUi.getOrAwaitValue()
            .partition { it.isSelected }
        assertTrue(selectedDice.all { it in mockDiceList })
        assertTrue(newGeneratedDice.none { it in mockDiceList })

    }

    @Test
    fun givenAllDicesAreSelectedWithSameValuesWhenRollDiceThenGameIsOverAndResetGame() {
        val mockDiceList = getRandomList(isGameOver = true)
        viewModel = DiceViewModel(mockDiceList)

        viewModel.rollDice()

        val resetList = viewModel.diceUi.getOrAwaitValue()
        assertNotEquals(mockDiceList, resetList)
        assertFalse(viewModel.isGameOver.getOrAwaitValue())
        assertEquals(
            R.raw.rollingdice, viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled()
        )


    }


//
//    @Test
//    fun `rollDice does nothing if game over`() {
//        val mockDiceList = List(10) { Dice(value = 5, isSelected = true) }
//        `when`(diceGenerator.generate()).thenReturn(mockDiceList)
//        viewModel.generateNewDice()
//        viewModel.rollDice()
//
//    }
//
//    @Test
//    fun `rollDice should update dice list and play sound`() {
//        // Act
//        viewModel.rollDice()
//        //assertNotEquals(diceList, viewModel.diceUi.getOrAwaitValue())
//        val expectedEvent = viewModel.soundEvent.getOrAwaitValue()
//        assertEquals(expectedEvent.getContentIfNotHandled(), R.raw.rollingdice)
//    }
//
//    @Test
//    fun `restartGame should reset the dice list and isGameOver flag`() {
//
//        // Simulate game over
//        `when`(diceGenerator.generate()).thenReturn(
//            List(10) { Dice(id = it.toString(), value = 1, isSelected = true) }
//        )
//        viewModel.generateNewDice()
//        viewModel.rollDice()
//        // Assert
//        assertTrue(viewModel.isGameOver.getOrAwaitValue())
//        assertEquals(
//            R.raw.goodresult,
//            viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled()
//        )
//        assertNotNull(viewModel.onGameFinished.getOrAwaitValue().getContentIfNotHandled())
//        verify(diceGenerator, times(2)).generate()
//    }
//
//
//    @Test
//    fun `should play sound when roll dice`() {
//        // Act
//        viewModel.rollDice()
//        // Assert
//        val expectedEvent = viewModel.soundEvent.getOrAwaitValue()
//        assertEquals(expectedEvent.getContentIfNotHandled(), R.raw.rollingdice)
//    }
//
//    @Test
//    fun whenDiceIsNotSelectedGenerateNewDice() {
//        val diceList = List(10) {
//            Dice()
//        }
//        `when`(diceGenerator.generate()).thenReturn(diceList)
//        viewModel.generateNewDice()
//        `when`(diceGenerator.generateSingleDie()).thenReturn(Dice())
//
//        viewModel.rollDice()
//        val expected = viewModel.diceUi.getOrAwaitValue().map { it.id }
//        assertNotEquals(expected, diceList.map { it.id })
//    }
//
//    @Test
//    fun whenAllDiceHaveSameValueGameIsOver() {
//        val diceList = List(10) {
//            Dice(value = 3)
//        }
//        `when`(diceGenerator.generate()).thenReturn(diceList)
//        viewModel.generateNewDice()
//
//        diceList.forEach {
//            viewModel.holdDice(it.id)
//        }
//
//        assertTrue(viewModel.isGameOver.getOrAwaitValue())
//    }
//
//
//
//    @Test
//    fun givenAllDiceAreHeldWhenHoldDiceAgainThenNoSound() {
//        val diceList = List(5) {
//            Dice(isSelected = true, value = 5)
//        } + List(5) {
//            Dice(isSelected = true, value = 1)
//        }
//        `when`(diceGenerator.generate()).thenReturn(diceList)
//        viewModel.generateNewDice()
//
//        viewModel.holdDice(diceList.first().id)
//        val expected = viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled()
//        assertEquals(expected, null)
//    }

}
