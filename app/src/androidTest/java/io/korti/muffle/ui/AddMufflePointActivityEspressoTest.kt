package io.korti.muffle.ui

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import io.korti.muffle.MainActivity
import io.korti.muffle.R
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddMufflePointActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        ACCESS_FINE_LOCATION,
        ACCESS_BACKGROUND_LOCATION
    )

    @Test fun onBackButtonPress() {
        onView(withId(R.id.fab)).perform(click())
        onView(withText(R.string.title_activity_add_muffle_point)).check(matches(isDisplayed()))
        onView(withContentDescription("Navigate up")).perform(click())
        onView(withText(R.string.app_name)).check(matches(isDisplayed()))
    }

    @Test fun onSaveButtonPress() {
        onView(withId(R.id.fab)).perform(click())
        onView(withText(R.string.title_activity_add_muffle_point)).check(matches(isDisplayed()))
        onView(withId(R.id.action_save)).perform(click())
        onView(withText("New muffle point added."))
            .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))
        onView(withText(R.string.app_name)).check(matches(isDisplayed()))
    }

}