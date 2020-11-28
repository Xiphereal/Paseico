package app.paseico;

import android.widget.TextView;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.paseico.login.LogInActivity;
import app.paseico.login.RegisterActivity;
import app.paseico.mainMenu.marketplace.DiscountsFragment;
import app.paseico.mainMenu.marketplace.marketplaceFragment;

import static androidx.test.InstrumentationRegistry.getTargetContext;
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
public class MarketPlaceTest {
    @Rule
    public FragmentTestRule<?, DiscountsFragment> fragmentTestRule =
            FragmentTestRule.create(DiscountsFragment.class);

     @Before
    public void init(){
         fragmentTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void buy1000PointsTest() throws Exception{


         onView(withId(R.id.imageViewBuy1000Points)).perform(click());
         onView(withId(R.id.textViewMarketplacePTS)).check(matches(isDisplayed()));

    }

}
