package app.paseico;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import app.paseico.login.LogInActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<LogInActivity> mActivityRule =
            new ActivityTestRule<>(LogInActivity.class);

    @Before
    public void beforeEach() {
        Intents.init();
    }

    @Test
    public void loginSuccessful() throws InterruptedException {
        onView(withId(R.id.editTextEmail))
                .perform(typeText("metralleta123@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("password11"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogIn))
                .perform(click());

        // Workaround for waiting to the login to be processed.
        Thread.sleep(2000);

        // Check if the previous intent is for going to the Main Menu Activity.
        intended(hasComponent(MainMenuActivity.class.getName()));
    }

    @Test
    public void loginNotSuccessful() throws InterruptedException {
        onView(withId(R.id.editTextEmail))
                .perform(typeText("miguelmoreno99@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("nomelase"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogIn))
                .perform(click());

        // Workaround for waiting to the login to be processed.
        Thread.sleep(2000);

        // Check if the login screen is still present, with the "PASEICO" text being displayed.
        onView(withText("PASEICO")).check(matches(isDisplayed()));
    }

    @Test
    public void emptyLogIn() throws InterruptedException {
        onView(withId(R.id.editTextEmail))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.buttonLogIn))
                .perform(click());

        // Workaround for waiting to the login to be processed.
        Thread.sleep(2000);

        // Check if the login screen is still present, with the "PASEICO" text being displayed.
        onView(withText("PASEICO")).check(matches(isDisplayed()));
    }

    @After
    public void afterEach() {
        Intents.release();
    }
}

