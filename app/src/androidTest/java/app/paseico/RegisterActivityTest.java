package app.paseico;


import android.content.ComponentName;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.rule.ActivityTestRule;

import app.paseico.login.RegisterActivity;


import static androidx.test.espresso.action.;
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void submitRegisterCorrectly() throws InterruptedException {
       onView(withId(R.id.editTextUsername))
               .perform(typeText("Joselito699hd"), closeSoftKeyboard());
       onView(withId(R.id.editTextPassword))
               .perform(typeText("password123"), closeSoftKeyboard());
       onView(withId(R.id.editTextPasswordConf))
               .perform(typeText("password123"),closeSoftKeyboard());
       onView(withId(R.id.editTextName))
               .perform(typeText("Jose"),closeSoftKeyboard());
       onView(withId(R.id.editTextSurname))
               .perform(typeText("romero mohedano"),closeSoftKeyboard());
       onView(withId(R.id.editTextEmail))
               .perform(typeText("emaildejoseinventado5556696@gmail.com"), closeSoftKeyboard());
       onView(withId(R.id.buttonRegister))
               .perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ExpectedActivity.class)));


    }

}
