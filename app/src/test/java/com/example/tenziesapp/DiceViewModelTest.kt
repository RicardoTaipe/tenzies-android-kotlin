package com.example.tenziesapp


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeoutException

@RunWith(MockitoJUnitRunner::class)
class DiceViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DiceViewModel
    private fun getRandomList(
        isGameOver: Boolean = false,
        allSelected: Boolean = false,
        someSelected: Boolean = false,
        allSameValue: Boolean = false,
    ): List<Dice> {
        return when {
            allSameValue -> List(10) { Dice(value = 6) }
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

    /**
     * HOLD ACTION
     * */

    @Test
    fun givenRandomDiceListWhenOneOrMoreDiceAreHeldThenDiceShouldBeSelected() {
        val mockDiceList = getRandomList()
        viewModel = DiceViewModel(mockDiceList)

        val selectedDiceCount = 3
        mockDiceList.take(selectedDiceCount).forEach {
            viewModel.holdDice(it.id)
        }

        val actualCount = viewModel.diceUi.getOrAwaitValue().filter { it.isSelected }.size
        assertEquals(selectedDiceCount, actualCount)
    }


    @Test
    fun givenAllDiceSelectedWithDifferentValuesWhenOneOrMoreDiceAreUnselectedThenDiceShouldBeUnSelected() {
        val mockDiceList = getRandomList(allSelected = true)
        viewModel = DiceViewModel(mockDiceList)

        val unselectedDiceCount = 4
        mockDiceList.take(unselectedDiceCount).forEach {
            viewModel.holdDice(it.id)
        }

        val actualCount = viewModel.diceUi.getOrAwaitValue().filter { it.isUnSelected }.size
        assertEquals(unselectedDiceCount, actualCount)
    }

    @Test
    fun givenDiceListWithDifferentValuesWhenOneOrMoreAreSelectedThenGameIsNotOver() {
        val mockDiceList = getRandomList()
        viewModel = DiceViewModel(mockDiceList)

        mockDiceList.forEach {
            viewModel.holdDice(it.id)
        }

        assertFalse(viewModel.isGameOver.getOrAwaitValue())
    }

    @Test
    fun givenAllDiceWithSameValueWhenAllAreSelectedThenGameIsOverAndPlaySound() {
        val mockDiceList = getRandomList(allSameValue = true)
        viewModel = DiceViewModel(mockDiceList)
        mockDiceList.forEach {
            viewModel.holdDice(it.id)
        }
        assertTrue(viewModel.isGameOver.getOrAwaitValue())
        assertEquals(
            R.raw.goodresult,
            viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled()
        )
        assertNotNull(viewModel.onGameFinished.getOrAwaitValue().getContentIfNotHandled())
    }

    @Test
    fun givenGameIsOverWhenADiceIsSelectedShouldNotSelectAnyDice() {
        val mockDiceList = getRandomList(isGameOver = true)
        viewModel = DiceViewModel(mockDiceList)
        viewModel.holdDice(mockDiceList.first().id)

        assertEquals(mockDiceList, viewModel.diceUi.getOrAwaitValue())
    }

    /**
     * ROLL ACTION
     * */
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
    fun givenAllDicesAreSelectedWithSameValuesWhenRollDiceThenResetGame() {
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

    @Test
    fun givenAllDicesAreSelectedWithDifferentValuesWhenRollDiceThenDoNotPlaySound() {
        val mockDiceList = getRandomList(allSelected = true)
        viewModel = DiceViewModel(mockDiceList)

        viewModel.rollDice()

        val resetList = viewModel.diceUi.getOrAwaitValue()
        assertEquals(mockDiceList, resetList)
        assertThrows("Sound Event should not be called", TimeoutException::class.java) {
            viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled()
        }
    }
}
