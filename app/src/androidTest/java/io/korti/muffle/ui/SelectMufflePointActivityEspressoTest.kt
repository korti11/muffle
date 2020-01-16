package io.korti.muffle.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.korti.muffle.AddMufflePointActivity
import io.korti.muffle.R
import org.junit.Rule
import org.junit.Test

class SelectMufflePointActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityTestRule(AddMufflePointActivity::class.java)

    @Test
    fun onBackButtonPress() {
        onView(withId(R.id.selectActivityButton)).perform(click())
        onView(withText(R.string.title_activity_select_muffle_point))
            .check(matches(isDisplayed()))
        onView(withContentDescription("Navigate up"))
            .perform(click())
        onView(withText(R.string.title_activity_add_muffle_point))
            .check(matches(isDisplayed()))
    }

    @Test
    fun onSaveButtonPress() {
        onView(withId(R.id.selectActivityButton)).perform(click())
        onView(withText(R.string.title_activity_select_muffle_point))
            .check(matches(isDisplayed()))
        onView(withId(R.id.action_save)).perform(click())
        Thread.sleep(1000)
        onView(withText(R.string.title_activity_add_muffle_point))
            .check(matches(isDisplayed()))
    }

}