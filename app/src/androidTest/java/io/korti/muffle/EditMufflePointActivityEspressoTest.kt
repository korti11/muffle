package io.korti.muffle

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import io.korti.muffle.adapter.MuffleCardAdapter
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class EditMufflePointActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        ACCESS_FINE_LOCATION,
        ACCESS_BACKGROUND_LOCATION
    )

    @Test
    fun onBackButtonPress() {
        onView(withId(R.id.muffleCards)).
            perform(scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(1))
        onView(withChild(withText("Home"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.editButton), isDescendantOfA(withChild(withText("Home")))))
            .perform(ViewActions.click())
        onView(withText(R.string.title_activity_edit_muffle_point))
            .check(matches(isDisplayed()))
        onView(withId(R.id.muffleName))
            .check(matches(withText("Home")))
        onView(withContentDescription("Navigate up"))
            .perform(ViewActions.click())
        onView(withText(R.string.app_name))
            .check(matches(isDisplayed()))
    }

    @Test
    fun onSaveButtonPress() {
        onView(withId(R.id.muffleCards)).
            perform(scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(1))
        onView(withChild(withText("Home"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.editButton), isDescendantOfA(withChild(withText("Home")))))
            .perform(ViewActions.click())
        onView(withText(R.string.title_activity_edit_muffle_point))
            .check(matches(isDisplayed()))
        onView(withId(R.id.muffleName))
            .check(matches(withText("Home")))
        onView(withId(R.id.action_save)).perform(ViewActions.click())
        onView(withText("Edited muffle point saved."))
            .inRoot(withDecorView(not(`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))
        onView(withText(R.string.app_name))
            .check(matches(isDisplayed()))
    }

}