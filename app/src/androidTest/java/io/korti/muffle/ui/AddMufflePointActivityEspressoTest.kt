package io.korti.muffle.ui

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import io.korti.muffle.MainActivity
import io.korti.muffle.MuffleApplication
import io.korti.muffle.R
import io.korti.muffle.adapter.MuffleCardAdapter
import org.junit.After
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

    @After
    fun deleteTestData() {
        delete("jku")
    }

    private fun delete(mufflePointId: String) {
        val mufflePointDao = MuffleApplication.getDatabase().getMufflePointDao()
        val point = mufflePointDao.getById(mufflePointId)
        if(point != null) {
            mufflePointDao.delete(point)
        }
    }

    @Test fun onBackButtonPress() {
        onView(withId(R.id.fab)).perform(click())
        onView(withText(R.string.title_activity_add_muffle_point)).check(matches(isDisplayed()))
        onView(withContentDescription("Navigate up")).perform(click())
        onView(withText(R.string.app_name)).check(matches(isDisplayed()))
    }

    @Test fun onSaveButtonPress() {
        onView(withId(R.id.fab)).perform(click())
        onView(withText(R.string.title_activity_add_muffle_point)).check(matches(isDisplayed()))
        onView(withId(R.id.muffleName)).perform(typeText("JKU"))
        onView(withId(R.id.action_save)).perform(click())
        Thread.sleep(1500) // TODO: Replace this sometime with idle resources.
        onView(withId(R.id.muffleCards)).
            perform(RecyclerViewActions.scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(0))
        onView(withText("JKU")).check(matches(isDisplayed()))
        onView(withText(R.string.app_name)).check(matches(isDisplayed()))
    }

}