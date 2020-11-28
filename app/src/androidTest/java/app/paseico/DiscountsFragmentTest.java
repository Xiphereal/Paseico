package app.paseico;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.paseico.mainMenu.marketplace.DiscountsFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DiscountsFragmentTest {

    @Rule
    public FragmentTestRule<?, DiscountsFragment> fragmentTestRule =
            FragmentTestRule.create(DiscountsFragment.class);



    @Test
    public void clickPnItemListView() throws Exception{
        onView(withId(R.id.ListViewDiscounts)).check(matches(isDisplayed()));

    }

}
