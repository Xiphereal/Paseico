package app.paseico;

import org.junit.Rule;
import org.junit.Test;

import app.paseico.mainMenu.marketplace.DiscountsFragment;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;

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

    @Test
    public void clickOnItem(){
        mFragmentTestRule.launchActivity(null);
        onData(instanceOf(String.class)).atPosition(0);
        //onData(anything()).inAdapterView(withId(R.id.ListViewDiscounts)).atPosition(0).perform(click());
    }
}
