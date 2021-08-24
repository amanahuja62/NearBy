package com.example.nearby.user.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nearby.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SignUpActivityTest {

    @Before
    fun method(){
        val activityRule = ActivityScenario.launch(SignUpActivity::class.java)

    }

    @Test
    fun test_checkActivityVisibility(){
        onView(withId(R.id.signupPage)).check(matches(isDisplayed()))
    }

    @Test
    fun test_checkIfAllComponentsAreVisible(){
        onView(withId(R.id.username)).check(matches(isDisplayed()))
        onView(withId(R.id.email)).check(matches(isDisplayed()))
        onView(withId(R.id.pass)).check(matches(isDisplayed()))
        onView(withId(R.id.confirmPass)).check(matches(isDisplayed()))
        onView(withId(R.id.name)).check(matches(isDisplayed()))
    }

    @Test
    fun test_checkProgressBarVisibility_whenUserSignsUp(){
        onView(withId(R.id.username)).perform(typeText("aman"))
        onView(withId(R.id.email)).perform(typeText("aman.ahuja680@gmail.com"))
        onView(withId(R.id.pass)).perform(typeText("amanahuja123"))
        onView(withId(R.id.confirmPass)).perform(typeText("amanahuja123"))
        onView(withId(R.id.name)).perform(typeText("aman"))
        onView(withId(R.id.action_done)).perform(click())
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }

    @Test
    fun test_checkingResponse_whenPasswordIsLessThan_FiveCharacters(){
        onView(withId(R.id.name)).perform(typeText("aman"))
        onView(withId(R.id.username)).perform(typeText("aman"))
        onView(withId(R.id.email)).perform(typeText("aman.ahuja680@gmail.com"))
        onView(withId(R.id.pass)).perform(typeText("aman"))
        onView(withId(R.id.confirmPass)).perform(typeText("aman"))
        onView(withId(R.id.action_done)).perform(click())
        onView(withId(R.id.pass)).check(matches(hasErrorText("Password must have more than 4 characters")))
    }

}