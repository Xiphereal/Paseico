package app.paseico;

import android.app.Activity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import app.paseico.login.LogInActivity;
import app.paseico.login.RegisterActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void submitRegisterCorrectly(){
       onView(withId(R.id.editTextUsername))
               .perform(typeText("Joselito69hd"), closeSoftKeyboard());
       onView(withId(R.id.editTextPassword))
               .perform(typeText("password123"), closeSoftKeyboard());
       onView(withId(R.id.editTextPasswordConf))
               .perform(typeText("password103"),closeSoftKeyboard());
       onView(withId(R.id.editTextName))
               .perform(typeText("Jose"),closeSoftKeyboard());
       onView(withId(R.id.editTextSurname))
               .perform(typeText("romero mohedano"),closeSoftKeyboard());
       onView(withId(R.id.editTextEmail))
               .perform(typeText("emaildejoseinventado555666@gmail.com"), closeSoftKeyboard());
       onView(withId(R.id.buttonRegister))
               .perform(click());
       onView(withText("Registro completado!")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

}
