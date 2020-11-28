package app.paseico;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.core.AllOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.Router;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class CreateNewRouteRouterTest {

    @Rule
    public ActivityScenarioRule<CreateNewRouteActivity> mActivityRule =
            new ActivityScenarioRule<>(CreateNewRouteActivity.class);

    @Before
    public void logInAndPrepareRoute(){
        Router routerForTesting = new Router("Test","xXTesterXx","testing@test.ts");
        routerForTesting.setPoints(1000);
        List<PointOfInterest> pois4Testing = new ArrayList<>();
        pois4Testing.add(new PointOfInterest(20.0,20.0,"a"));
        pois4Testing.add(new PointOfInterest(21.0,21.0,"b"));
        pois4Testing.add(new PointOfInterest(22.0,22.0,"c"));
        //Route routeToCreate = new Route("route4Testing", pois4Testing, routerForTesting);
        //Log in with the local user?
//        onView(withId(R.id.editTextEmail))
//                .perform(typeText("userForTesting@gmail.com"), closeSoftKeyboard());
//        onView(withId(R.id.editTextPassword))
//                .perform(typeText("123456"), closeSoftKeyboard());
//        onView(withId(R.id.buttonLogIn))
//                .perform(click());
    }

    @Test
    public void createRouteSuccessfullyTest(){
        onView(withId(R.id.route_name_textInputEditText)).perform(typeText("myRoute"),closeSoftKeyboard());
        onView(withId(R.id.finalize_route_creation_button)).perform(click());
        onView(withText("Finalizar creación de ruta")).perform(click());
        onView(withText("Finalizar creación de ruta")).check(matches(isDisplayed()));
        //onView(withId(R.id.route_name_textInputEditText)).check(matches(allOf(withText("La nueva ruta ha sido guardada satisfactoriamente."),isDisplayed())));
        //onView(withText("La nueva ruta ha sido guardada satisfactoriamente.")).check(matches(isDisplayed()));
    }

    @Test
    public void createRouteNotSuccessfullyTest(){
        onView(withId(R.id.route_name_textInputEditText)).perform(typeText("myRoute"),closeSoftKeyboard());
        //need pois so the cost is greater than the user's amount of point
        //onView(withId(R.id.map)).perform(click());
        onView(withId(R.id.finalize_route_creation_button)).perform(click());
        onView(withText("Finalizar creación de ruta")).perform(click());
        onView(withText("No tienes los puntos suficientes para crear la ruta.")).check(matches(isDisplayed()));
    }

    @Test
    public void numberOfRoutesCreatedIncrementedTest(){

    }

}

