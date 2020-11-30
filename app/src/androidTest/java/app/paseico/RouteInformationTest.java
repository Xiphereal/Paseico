package app.paseico;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.mainMenu.searcher.RouteSearchFragment;
import app.paseico.service.FirebaseService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class RouteInformationTest {
    private final String expectedRouteName = "TestRoute";
    private final String expectedTheme = "Museos";
    private final double expectedLength = 200;
    private final double expectedEstimatedTime = 5;
    private final int expectedRewardPoints = 150;
    private final List<PointOfInterest> expectedPointsOfInterest = new ArrayList<>();

    private Route expectedRoute;

    @Rule
    public FragmentTestRule<?, RouteSearchFragment> fragmentTestRule =
            FragmentTestRule.create(RouteSearchFragment.class);

    @Before
    public void setUp() {
        createRoute();

        fragmentTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    private void createRoute() {
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

    @Test
    public void routeAllTheDetailedInformationIsCorrect() {
        onView(withId(R.id.editText_keyWord)).perform(typeText(expectedRouteName), closeSoftKeyboard());

        onView(withId(R.id.btn_search)).perform(click());

        onView(withText(expectedRouteName)).perform(click());
    }
}
