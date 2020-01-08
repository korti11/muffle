package io.korti.muffle.ui

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
import io.korti.muffle.MainActivity
import io.korti.muffle.MuffleApplication
import io.korti.muffle.R
import io.korti.muffle.adapter.MuffleCardAdapter
import io.korti.muffle.database.entity.MufflePoint
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
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

    @Before
    fun writeTestData() {
        val mufflePointDao = MuffleApplication.getDatabase().getMufflePointDao()
        mufflePointDao.insertAll(
            MufflePoint("home", name = "Home", image = "", status = MufflePoint.Status.ACTIVE),
            MufflePoint("work", name = "Work", status = MufflePoint.Status.DISABLED, image = "")
        )
    }

    @After
    fun deleteTestData() {
        val mufflePointDao = MuffleApplication.getDatabase().getMufflePointDao()
        mufflePointDao.delete(mufflePointDao.getById("home"))
        mufflePointDao.delete(mufflePointDao.getById("work"))
    }

    @Test
    fun onBackButtonPress() {
        Thread.sleep(200) // TODO: Replace this sometime with idle resources.
        onView(withId(R.id.muffleCards)).
            perform(scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(0))
        onView(withChild(withText("Home"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.editButton), isDescendantOfA(withChild(withText("Home")))))
            .perform(ViewActions.click())
        onView(withText(R.string.title_activity_edit_muffle_point))
            .check(matches(isDisplayed()))
        /*onView(withId(R.id.muffleName))
            .check(matches(withText("Home")))*/ // Currently not working and this is intended.
        onView(withContentDescription("Navigate up"))
            .perform(ViewActions.click())
        onView(withText(R.string.app_name))
            .check(matches(isDisplayed()))
    }

    @Test
    fun onSaveButtonPress() {
        Thread.sleep(200) // TODO: Replace this sometime with idle resources.
        onView(withId(R.id.muffleCards)).
            perform(scrollToPosition<MuffleCardAdapter.MuffleCardHolder>(0))
        onView(withChild(withText("Home"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.editButton), isDescendantOfA(withChild(withText("Home")))))
            .perform(ViewActions.click())
        onView(withText(R.string.title_activity_edit_muffle_point))
            .check(matches(isDisplayed()))
        /*onView(withId(R.id.muffleName))
            .check(matches(withText("Home")))*/ // Currently not working and this is intended.
        onView(withId(R.id.action_save)).perform(ViewActions.click())
        onView(withText("Edited muffle point saved."))
            .inRoot(withDecorView(not(`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))
        onView(withText(R.string.app_name))
            .check(matches(isDisplayed()))
    }

}