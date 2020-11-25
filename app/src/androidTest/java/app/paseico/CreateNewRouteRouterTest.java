package app.paseico;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.paseico.login.LogInActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class CreateNewRouteRouterTest {

    @Rule
    public ActivityTestRule<CreateNewRouteActivity> mActivityRule =
            new ActivityTestRule<>(CreateNewRouteActivity.class);

    @Test
    public void createRouteSuccessfullyTest(){

    }

    @Test
    public void createRouteNotSuccessfullyTest(){

    }

    @Test
    public void routeNumberIncrementedTest(){
        
    }

}

