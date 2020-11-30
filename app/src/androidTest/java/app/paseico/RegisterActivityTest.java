package app.paseico;


import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import app.paseico.login.LogInActivity;
import app.paseico.login.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

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
public class RegisterActivityTest {
    private FirebaseAuth firebaseAuth;
    private String randomName;

    @Before
    public void setRandomName() {
        Intents.init();
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        randomName = sb.toString();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void submitRegisterCorrectly() throws InterruptedException {

        onView(withId(R.id.editTextUsername))
                .perform(typeText(randomName), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordConf))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.editTextName))
                .perform(typeText("Jose"), closeSoftKeyboard());
        onView(withId(R.id.editTextSurname))
                .perform(typeText("romero mohedano"), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail))
                .perform(typeText(randomName + "@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.buttonRegister))
                .perform(click());
        onView(withText("Registro completado!")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        intended(hasComponent(LogInActivity.class.getName()));
    }

    @Test
    public void submitRegisterPassTooShort() throws InterruptedException {
        onView(withId(R.id.editTextUsername))
                .perform(typeText("Joselito420hd"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("pass1"), closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordConf))
                .perform(typeText("pass1"), closeSoftKeyboard());
        onView(withId(R.id.editTextName))
                .perform(typeText("Jose"), closeSoftKeyboard());
        onView(withId(R.id.editTextSurname))
                .perform(typeText("romero mohedano"), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail))
                .perform(typeText("emaildejoseinventado555666@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.buttonRegister))
                .perform(click());

        onView(withText("La contraseña debe contener mínimo 6 caracteres")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void submitEmptyRegister() {
        onView(withId(R.id.buttonRegister))
                .perform(click());

        onView(withText("Por favor, rellene todos los campos para poder registrarse")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void submitRegisterExistingUsername() {
        onView(withId(R.id.editTextUsername))
                .perform(typeText("miguelmoreno"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordConf))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.editTextName))
                .perform(typeText("miguel"), closeSoftKeyboard());
        onView(withId(R.id.editTextSurname))
                .perform(typeText("moreno"), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail))
                .perform(typeText("random@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.buttonRegister))
                .perform(click());

        onView(withText("Ese nombre de usuario ya existe")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void submitRegisterPassNotMatch() {
        onView(withId(R.id.editTextUsername))
                .perform(typeText("miguelmoreno"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.editTextPasswordConf))
                .perform(typeText("password127"), closeSoftKeyboard());
        onView(withId(R.id.editTextName))
                .perform(typeText("miguel"), closeSoftKeyboard());
        onView(withId(R.id.editTextSurname))
                .perform(typeText("moreno"), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail))
                .perform(typeText("random@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.buttonRegister))
                .perform(click());

        onView(withText("Las contraseñas no coinciden")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }
}





