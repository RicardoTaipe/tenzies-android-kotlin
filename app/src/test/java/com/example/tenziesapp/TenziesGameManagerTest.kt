package com.example.tenziesapp

import com.example.tenziesapp.GameUtils.createDice
import com.example.tenziesapp.GameUtils.initialNonWinningDice
import com.example.tenziesapp.GameUtils.matchingDiceNotLocked
import com.example.tenziesapp.GameUtils.winningDiceLocked
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test


class TenziesGameManagerTest {

    private val mockDiceGenerator = mockk<DiceGenerator>() // Mocks the dependency
    private lateinit var manager: TenziesGameManager // SUT (System Under Test)

    @Before
    fun setup() {
        clearAllMocks()
    }


    @Test
    fun `initial state should be non-winning and not game over`() {
        // Arrange
        every { mockDiceGenerator.generateNewDice() } returns initialNonWinningDice
        manager = TenziesGameManager(mockDiceGenerator)

        // Assert
        manager.currentDiceState.shouldContainExactly(initialNonWinningDice)
        manager.isGameOver.shouldBeFalse()

    }

    @Test
    fun `isGameOver should be true when all dice match and are locked (Winning State)`() {
        // Arrange
        every { mockDiceGenerator.generateNewDice() } returns winningDiceLocked
        manager = TenziesGameManager(mockDiceGenerator)

        // Assert
        manager.isGameOver.shouldBeTrue()
    }

    @Test
    fun `isGameOver should be false when dice match but not all are locked`() {
        // Arrange
        every { mockDiceGenerator.generateNewDice() } returns matchingDiceNotLocked
        manager = TenziesGameManager(mockDiceGenerator)

        // Assert
        manager.isGameOver.shouldBeFalse()
    }

    @Test
    fun `areAllDiceMatching should be false for an empty dice list (Edge Case)`() {
        // Arrange
        every { mockDiceGenerator.generateNewDice() } returns emptyList()
        manager = TenziesGameManager(mockDiceGenerator)

        // Assert
        manager.isGameOver.shouldBeFalse()
    }

    // --- Action Tests ---

    @Test
    fun `lockDice should toggle lock status when game is not over`() {
        // Arrange
        every { mockDiceGenerator.generateNewDice() } returns initialNonWinningDice
        manager = TenziesGameManager(mockDiceGenerator)

        // Lock a dice (d3 starts unlocked)
        manager.lockDice("d3")
        // Assert lock
        manager.currentDiceState.first { it.id == "d3" }.isSelected.shouldBeTrue()

        // Unlock the same dice
        manager.lockDice("d3")
        // Assert unlock
        manager.currentDiceState.first { it.id == "d3" }.isSelected.shouldBeFalse()
    }

    @Test
    fun `lockDice should do nothing when game is over (Edge Case)`() {

        val winningState = winningDiceLocked
        every { mockDiceGenerator.generateNewDice() } returns winningState
        manager = TenziesGameManager(mockDiceGenerator)

        // Pre-Assert: ensure game is over
        manager.isGameOver.shouldBeTrue()

        // Act: Try to toggle the lock status of a dice
        manager.lockDice("d1")

        // Assert: The lock status should not change (it should remain locked)
        manager.currentDiceState.first { it.id == "d1" }.isSelected.shouldBeTrue()
    }

    @Test
    fun `rollDice should roll unlocked dice and keep locked dice`() {
        // Arrange
        // Initial state: d1, d2 are locked (value 1, 2)
        val initialDice = listOf(
            createDice("d1", 1, true),
            createDice("d2", 2, true),
            createDice("d3", 3),
            createDice("d4", 4)
        )

        // Mock the results for the unlocked dice (d3)
        val rolledDice3 = createDice("new-d3", 5) // Mocked new dice for d3 slot

        every { mockDiceGenerator.generateNewDice() } returns initialDice
        every { mockDiceGenerator.generateSingleDice() } returns rolledDice3

        manager = TenziesGameManager(mockDiceGenerator)

        // Act
        manager.rollDice()

        // Assert: Check the state after the roll
        val newState = manager.currentDiceState

        // Locked dice (d1) should be unchanged
        newState.first { it.id == "d1" }.value shouldBe 1
        newState.first { it.id == "d1" }.isSelected.shouldBeTrue()

        // Unlocked dice (d3) should have the new rolled value
        newState.first { it.id == rolledDice3.id }.value shouldBe rolledDice3.value
        newState.first { it.id == rolledDice3.id }.isSelected.shouldBeFalse()
    }

    @Test
    fun `rollDice should restart the game when called after game over (Edge Case)`() {
        // Arrange
        // Initial state is the winning state
        every { mockDiceGenerator.generateNewDice() } returns winningDiceLocked andThen initialNonWinningDice
        manager = TenziesGameManager(mockDiceGenerator)

        // Pre-Assert: Game is over
        manager.isGameOver.shouldBeTrue()

        // Act
        manager.rollDice() // This should trigger a restartGame call

        // Assert: The state should now be the new set of non-winning dice
        manager.currentDiceState.shouldContainExactly(initialNonWinningDice)
        manager.isGameOver.shouldBeFalse()

        // Assert that the generator was called twice: once for setup, once for restart
        verify(exactly = 2) { mockDiceGenerator.generateNewDice() }
    }

    @Test
    fun `restartGame should reset state to a new set of dice`() {
        // Arrange
        // Manager starts with the winning state
        every { mockDiceGenerator.generateNewDice() } returns winningDiceLocked andThen initialNonWinningDice
        manager = TenziesGameManager(mockDiceGenerator)

        // Pre-Assert
        manager.isGameOver.shouldBeTrue()

        // Act
        manager.restartGame()

        // Assert: State is reset to the new dice and game is not over
        manager.currentDiceState.shouldContainExactly(initialNonWinningDice)
        manager.isGameOver.shouldBeFalse()
    }
}