//package com.example.tenziesapp
//
//import android.R
//import android.view.View
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.IdlingRegistry
//import androidx.test.espresso.NoMatchingViewException
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.contrib.RecyclerViewActions
//import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
//import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
//import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.filters.LargeTest
//import com.example.tenziesapp.util.DataBindingIdlingResource
//import com.example.tenziesapp.util.monitorActivity
//import org.hamcrest.Description
//import org.hamcrest.Matcher
//import org.hamcrest.Matchers.allOf
//import org.hamcrest.TypeSafeMatcher
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class MainActivityTest {
//    //private lateinit var diceGenerator: DiceGenerator
//
//    // An Idling Resource that waits for Data Binding to have no pending bindings
//    private val dataBindingIdlingResource = DataBindingIdlingResource()
//
//    @Before
//    fun init() {
//        ServiceLocator.diceGenerator = object : DiceGenerator {
//            override fun generateNewDice(): List<Dice> {
//                return List(10) { Dice(value = 5) }
//            }
//
//            override fun generateSingleDice(): Dice {
//                return Dice(value = 1)
//            }
//        }
//
//    }
//
//    /**
//     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
//     * are not scheduled in the main Looper (for example when executed on a different thread).
//     */
//    @Before
//    fun registerIdlingResource() {
//        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
//    }
//
//    /**
//     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
//     */
//    @After
//    fun unregisterIdlingResource() {
//        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
//    }
//
//    @Test
//    fun initGame() {
//        // Start up Activity
//        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
//        dataBindingIdlingResource.monitorActivity(activityScenario)
//
//
//        //onView(withId(R.id.roll_btn)).perform(click())
//
//        //onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withContentDescription("5"))))
//        // Asume que tu RecyclerView tiene un ID específico
//        // Asume que tu RecyclerView tiene un ID específico
//        val recyclerView = onView(withId(R.id.))
//
//        // Obtén el número de ítems en el RecyclerView
//
//        // Obtén el número de ítems en el RecyclerView
//        recyclerView.check { view: View, noViewFoundException: NoMatchingViewException? ->
//            val rv = view as RecyclerView
//            val itemCount = rv.adapter!!.itemCount
//            for (i in 0 until itemCount) {
//                // Desplaza al ítem i y verifica que su contentDescription sea "5"
//                recyclerView.perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(i))
//                onView(allOf(withId(R.id.dice_img), isDisplayed()))
//                    .check(matches(withContentDescription("5")))
//            }
//        }
//        // Make sure the activity is closed before resetting the db:
//        activityScenario.close()
//    }
//
//    // Custom Matcher to find ViewHolder containing an ImageView with the specific contentDescription
//    fun hasImageWithContentDescription(contentDescription: String): Matcher<View> {
//        return allOf(
//            isDescendantOfA(isAssignableFrom(RecyclerView::class.java)),
//            hasDescendant(withContentDescription(contentDescription))
//        )
//    }
//
//    // Custom matcher to check if a ViewHolder has a contentDescription of "5"
//    fun withContentDescriptionOf(value: String): TypeSafeMatcher<DiceViewHolder> {
//        return object : TypeSafeMatcher<DiceViewHolder>() {
//            override fun matchesSafely(item: DiceViewHolder): Boolean {
//                val targetView = item.itemView.findViewById<View>(R.id.dice_img)
//                return targetView.contentDescription == value
//            }
//
//            override fun describeTo(description: Description) {
//                description.appendText("ViewHolder with contentDescription: $value")
//            }
//        }
//    }
//}