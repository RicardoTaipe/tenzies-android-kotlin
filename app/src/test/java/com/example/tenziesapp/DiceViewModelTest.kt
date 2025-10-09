package com.example.tenziesapp


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.tenziesapp.GameUtils.initialNonWinningDice
import com.example.tenziesapp.GameUtils.matchingDiceNotLocked
import com.example.tenziesapp.GameUtils.winningDiceLocked
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DiceViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val mockGameManager = mockk<GameManager>(relaxed = true)
    private lateinit var viewModel: DiceViewModel


    @Before
    fun setup() {
        clearAllMocks()
        // Default setup for the initial state of the ViewModel
        every { mockGameManager.currentDiceState } returns initialNonWinningDice
        every { mockGameManager.isGameOver } returns false
        justRun { mockGameManager.rollDice() }
        justRun { mockGameManager.lockDice(any()) }
        viewModel = DiceViewModel(mockGameManager)
    }

    @Test
    fun `initial state should load current state from GameManager`() {
        // Assert initial values
        viewModel.diceUi.getOrAwaitValue().shouldBe(initialNonWinningDice)
        viewModel.isGameOver.getOrAwaitValue().shouldBeFalse()

        // Assert that sound/event live data is null/empty initially
        viewModel.soundEvent.value.shouldBe(null)
        viewModel.onGameFinished.value.shouldBe(null)
    }

    @Test
    fun `rollDice should delegate to GameManager and update UI (Non-winning roll)`() {
        // Arrange
        // Mock the state *after* the rollDice() call
        every { mockGameManager.currentDiceState } returns initialNonWinningDice // Assume roll changes state

        // Act
        viewModel.rollDice()

        // Assert
        verify(exactly = 1) { mockGameManager.rollDice() }

        // UI State Updated
        viewModel.diceUi.getOrAwaitValue().shouldBe(initialNonWinningDice)
        viewModel.isGameOver.getOrAwaitValue().shouldBeFalse()

        // Sound Event (Rolling sound should play because dice are NOT locked)
        viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled() shouldBe R.raw.rollingdice
    }

    @Test
    fun `lockDice should delegate to GameManager and update UI (Game still active)`() {
        // Arrange
        val diceId = "d1"
        // Mock the state *after* the lockDice() call
        every { mockGameManager.currentDiceState } returns matchingDiceNotLocked
        every { mockGameManager.isGameOver } returns false

        // Act
        viewModel.lockDice(diceId)

        // Assert
        verify(exactly = 1) { mockGameManager.lockDice(diceId) }

        // UI State Updated
        viewModel.diceUi.getOrAwaitValue().shouldBe(matchingDiceNotLocked)
        viewModel.isGameOver.getOrAwaitValue().shouldBeFalse()

        // Ensure no events were fired
        viewModel.soundEvent.value.shouldBe(null)
        viewModel.onGameFinished.value.shouldBe(null)
    }

    @Test
    fun `updateUiState should trigger game over events when state transitions to game over`() {
        // Arrange
        // Ensure initial state is NOT game over
        viewModel.isGameOver.getOrAwaitValue().shouldBeFalse()

        // Mock the transition: state is now WINNING
        every { mockGameManager.isGameOver } returns true
        every { mockGameManager.currentDiceState } returns winningDiceLocked

        // Act: Call updateUiState() directly to test transition logic
        viewModel.rollDice() // rollDice calls updateUiState internally

        // Assert
        // UI State Updated
        viewModel.diceUi.getOrAwaitValue().shouldBe(winningDiceLocked)
        viewModel.isGameOver.getOrAwaitValue().shouldBeTrue()

        // Sound Event (Winning sound should play)
        viewModel.soundEvent.getOrAwaitValue().getContentIfNotHandled() shouldBe R.raw.goodresult

        // Game Finished Event (Should fire once)
        viewModel.onGameFinished.getOrAwaitValue().getContentIfNotHandled().shouldNotBe(null)
    }
}
