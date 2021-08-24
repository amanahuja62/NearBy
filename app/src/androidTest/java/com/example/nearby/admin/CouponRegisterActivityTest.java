package com.example.nearby.admin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.nearby.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class CouponRegisterActivityTest {

    @Rule
    // third parameter is set to false which means the activity is not started automatically
    public ActivityTestRule<CouponRegisterActivity> rule =
            new ActivityTestRule(CouponRegisterActivity.class, false, false){
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra("mode", "create");
                    return intent;
                }
            };

    @Test
    public void demonstrateIntentPrep() {

        onView(withId(R.id.linearly)).check(matches(isDisplayed()));

    }


}