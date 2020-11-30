package app.paseico;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.paseico.login.LogInActivity;
import app.paseico.login.RegisterActivity;

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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<LogInActivity> mActivityRule =
            new ActivityTestRule<>(LogInActivity.class);

    @Test
    public void loginSuccessful(){
        onView(withId(R.id.editTextEmail))
                .perform(typeText("metralleta123@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("password11"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogIn))
                .perform(click());

        onView(withText("¡Bienvenido de nuevo!")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void loginNotSuccessful(){
        onView(withId(R.id.editTextEmail))
                .perform(typeText("miguelmoreno99@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("nomelase"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogIn))
                .perform(click());

        onView(withText("Correo electronico o contraseña incorrectos!")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void emptyLogIn(){
        onView(withId(R.id.editTextEmail))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.buttonLogIn))
                .perform(click());

        onView(withText("Faltan campos por rellenar!")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

}

