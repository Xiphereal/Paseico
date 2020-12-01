package app.paseico;


import android.Manifest;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;

public class RouteInformationTest {
    private static final String expectedRouteName = "TestRoute";
    private static final String expectedTheme = "Museos";
    private static final double expectedLength = 200;
    private static final double expectedEstimatedTime = 5;
    private static final int expectedRewardPoints = 150;
    private static final List<PointOfInterest> expectedPointsOfInterest = new ArrayList<>();

    private static Route expectedRoute;

    @Rule
    public ActivityTestRule<MainMenuActivity> mainMenuActivityActivityTestRule =
            new ActivityTestRule<>(MainMenuActivity.class);

    @BeforeClass
    public static void beforeClass() {
        createRoute();
    }

    private static void createRoute() {
        expectedPointsOfInterest.add(new PointOfInterest(39.66290265223034, -0.25573253631591797, "testPoi1"));
        expectedPointsOfInterest.add(new PointOfInterest(39.47360355556778, -0.37898540496826166, "testPoi2"));
        expectedPointsOfInterest.add(new PointOfInterest(39.47596848025821, -0.3752410411834717, "testPoi3"));

        expectedRoute = new Route(expectedRouteName,
                expectedTheme,
                expectedLength,
                expectedEstimatedTime,
                expectedRewardPoints,
                expectedPointsOfInterest,
                null);

        FirebaseService.saveRoute(expectedRoute);
    }

    @Before
    public void beforeEach() {
        loginAsTestingUser();
    }

    private void loginAsTestingUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String email = "avd@gmail.com";
        String password = "123456";
        firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    @Test
    public void routeAllTheDetailedInformationIsCorrect() throws InterruptedException {
        navigateToRouteInformation();

        // Assert the results
        onView(withId(R.id.textView_routeInfo_nameOfRoute)).check(matches(withText(expectedRouteName)));
        onView(withId(R.id.textView_routeInfo_theme)).check(matches(withText(expectedTheme)));

        // Calculate the lenght like "XX km y XX metros"
        // and estimatedTime like " hh horas y mm minutos"
        int kms = (int) expectedLength / 1000;
        int meters = (int) expectedLength % 1000;
        String length = kms + " km y " + meters + " metros";
        int hours = ((int) expectedEstimatedTime) / 60;
        int minutes = ((int) expectedEstimatedTime) % 60;
        String estimatedTime = hours + " horas y " + minutes + " minutos";

        onView(withId(R.id.textView_routeInfo_length)).check(matches(withText(length)));
        onView(withId(R.id.textView_routeInfo_estimatedTime)).check(matches(withText(estimatedTime)));
        onView(withId(R.id.textView_routeInfo_rewardPoints)).check(matches(withText(String.valueOf(expectedRewardPoints))));
        onView(withId(R.id.textView_routeInfo_numberOfPOI)).check(matches(withText(String.valueOf(expectedPointsOfInterest.size()))));

        Thread.sleep(2000);
    }

    private void navigateToRouteInformation() throws InterruptedException {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.RouteSearchFragment));

        onView(withId(R.id.editText_keyWord)).perform(typeText(expectedRouteName), closeSoftKeyboard());

        onView(withId(R.id.button_route_searcher)).perform(click());

        Thread.sleep(2000);
        onData(anything()).atPosition(0).perform(click());
    }

    @Test
    public void canInitiateRouteRunner() throws InterruptedException{
        navigateToRouteInformation();

        onView(withId(R.id.btn_routeInfo_startRoute)).perform(click());
        Thread.sleep(2000);

        // Assert the results
        onView(withId(R.id.textViewTitleRoutingActivity)).check(matches(isDisplayed()));
    }

    @AfterClass
    public static void afterClass() {
        deleteExpectedRouteFromDatabase();
    }

    private static void deleteExpectedRouteFromDatabase() {
        FirebaseService.deleteRoute(expectedRoute);
    }
}
