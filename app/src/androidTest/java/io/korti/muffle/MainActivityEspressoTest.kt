package io.korti.muffle

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import io.korti.muffle.adapter.MuffleCardAdapter
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test fun disableMufflePoint() {
        onView(withId(R.id.muffleCards)).
            perform(RecyclerViewActions.scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(1))
        onView(withChild(withText("Home"))).check(matches(isDisplayed()))
        onView(allOf(withText("Disable"), isDescendantOfA(allOf(withChild(withText("Home")), withChild(
            withText("Status: Active")))))).perform(click())
        onView(allOf(withId(R.id.muffleStatus), isDescendantOfA(withChild(withText("Home"))))).check(
            matches(withText("Status: Disabled")))
    }

    @Test fun enableMufflePoint() {
        onView(withId(R.id.muffleCards)).
            perform(RecyclerViewActions.scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(2))
        onView(withChild(withText("Work"))).check(matches(isDisplayed()))
        onView(allOf(withText("Enable"), isDescendantOfA(allOf(withChild(withText("Work")), withChild(
            withText("Status: Disabled")))))).perform(click())
        onView(allOf(withId(R.id.muffleStatus), isDescendantOfA(withChild(withText("Work"))))).check(
            matches(withText("Status: Not active")))
    }

    @Test fun onClickEdit() {
        onView(withId(R.id.muffleCards)).
            perform(RecyclerViewActions.scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(1))
        onView(allOf(withId(R.id.editButton), isDescendantOfA(withChild(withText("Home")))))
            .perform(click())
        onView(withText(R.string.title_activity_edit_muffle_point))
            .check(matches(isDisplayed()))
        onView(withId(R.id.muffleName))
            .check(matches(withText("Home")))
    }

    @Test fun onClickFAB() {
        onView(withId(R.id.fab)).perform(click())
        onView(withText("Create")).check(matches(isDisplayed()))
    }

    @Test fun onClickSync() {
        openActionBarOverflowOrOptionsMenu(activityRule.activity)
        onView(withText(R.string.action_sync)).perform(click())
        onView(withText("Sync with firebase."))
            .inRoot(withDecorView(not(`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))
        Thread.sleep(200) // Not good but easier then idle resources for just waiting that a toast disappear
    }

    @Test fun onClickSettings() {
        openActionBarOverflowOrOptionsMenu(activityRule.activity)
        onView(withText(R.string.action_settings)).perform(click())
        onView(withText("Open SettingsActivity."))
            .inRoot(withDecorView(not(`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))
        Thread.sleep(200) // Not good but easier then idle resources for just waiting that a toast disappear
    }
}