package com.example.tenziesapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isSelected
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.tenziesapp.GameUtils.initialNonWinningDice
import com.example.tenziesapp.util.DataBindingIdlingResource
import com.example.tenziesapp.util.monitorActivity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    private lateinit var mockGameManager: GameManager

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        mockGameManager = mockk<GameManager>(relaxed = true)
        every { mockGameManager.currentDiceState } returns initialNonWinningDice
        every { mockGameManager.isGameOver } returns false

        ServiceLocator.gameManager = mockGameManager
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun initGame_showsWinningState() {
        every { mockGameManager.isGameOver } returns true
        every { mockGameManager.currentDiceState } returns GameUtils.winningDiceLocked

        val activityScenario = launchActivity()

        onView(withId(R.id.roll_btn)).check(matches(withText(R.string.game_over)))
        onView(withId(R.id.confetti)).check(matches(isDisplayed()))

        GameUtils.winningDiceLocked.forEach {
            onView(allOf(withTagValue(`is`(it.id)))).check(
                matches(
                    allOf(
                        isSelected(),
                        withContentDescription(it.value.toString())
                    )
                )
            )
        }
        activityScenario.close()
    }

    @Test
    fun diceClick_callsLockDiceAndUpdatesUI() {
        val diceToLock = initialNonWinningDice.first()
        every { mockGameManager.currentDiceState } returns initialNonWinningDice.map {
            if (it.id == diceToLock.id) it.copy(
                isSelected = true
            ) else it
        }

        val activityScenario = launchActivity()

        onView(allOf(withId(R.id.dice_img), withTagValue(`is`(diceToLock.id)))).perform(click())

        verify { mockGameManager.lockDice(diceToLock.id) }

        onView(allOf(withTagValue(`is`(diceToLock.id)))).check(
            matches(
                allOf(isSelected(), withContentDescription(diceToLock.value.toString()))
            )
        )

        activityScenario.close()
    }

    private fun launchActivity(): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)
        return scenario
    }
}