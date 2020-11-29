package app.paseico;

import android.os.Handler;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import app.paseico.mainMenu.marketplace.DiscountsFragment;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

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
    public void performClick() throws InterruptedException {
        //ListView is an AdapterView. AdapterViews present a problem when doing UI testing. Since an AdapterView doesn't load all of it's items upfront (only as the user scrolls through the items), AdapterView's don't work well with onView(...) since the particular view might not be part of the view hierarchy yet.
        //Fortunately, Espresso provides an onData(...) entry point that makes sure to load the AdapterView item before performing any operations on it.
        //onData(instanceOf(DiscountObj.class)).atPosition(0);
        mFragmentTestRule.launchActivity(null);

        Thread.sleep(5000);
        
        onView(withId(R.id.ListViewDiscounts)).check(matches(isDisplayed()));

        onData(anything())
                .inAdapterView(withId(R.id.ListViewDiscounts))
                .atPosition(0).perform(click());


    }







}
