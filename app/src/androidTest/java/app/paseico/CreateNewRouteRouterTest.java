package app.paseico;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

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
        //pois4Testing.
        //Route routeToCreate = new Route("route4Testing",,routerForTesting);
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
    }

    @Test
    public void createRouteNotSuccessfullyTest(){

    }

    @Test
    public void numberOfRoutesCreatedIncrementedTest(){

    }

}

