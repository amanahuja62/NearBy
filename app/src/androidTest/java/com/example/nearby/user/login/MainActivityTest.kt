package com.example.nearby.user.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nearby.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Test
    fun test_isActivityInView(){
        val activityRule = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.relativeLayout)).check(matches(isDisplayed()))
    }
    /*
          This function checks if all components of MainActivity like, imageview, editext, textview are
          displayed or not
         */
    @Test
    fun test_areAllComponenetsDisplayed(){
        val activityRule = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.nearbyImage)).check(matches(isDisplayed()))
        onView(withId(R.id.username)).check(matches(isDisplayed()))
        onView(withId(R.id.password)).check(matches(isDisplayed()))
        onView(withId(R.id.signin)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()))
    }

    @Test
    fun test_signupNavigation(){
        val activityRule = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.sign_up)).perform(click())
        onView(withId(R.id.signupPage)).check(matches(isDisplayed()))
    }


}