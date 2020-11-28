package app.paseico;

import org.junit.Rule;
import org.junit.Test;

import app.paseico.mainMenu.marketplace.DiscountsFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MyFragmentTest {

    @Rule
    public FragmentTestRule<DiscountsFragment> mFragmentTestRule = new FragmentTestRule<>(DiscountsFragment.class);

    @Test
    public void fragment_can_be_instantiated() {

        // Launch the activity to make the fragment visible
        mFragmentTestRule.launchActivity(null);

        // Then use Espresso to test the Fragment
        onView(withId(R.id.ListViewDiscounts)).check(matches(isDisplayed()));
    }
}
