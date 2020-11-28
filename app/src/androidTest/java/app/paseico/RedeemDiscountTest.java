package app.paseico;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Rule;
import org.junit.runner.RunWith;

import app.paseico.mainMenu.marketplace.DiscountsFragment;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)

public class RedeemDiscountTest {

    @Rule
    public FragmentTestRule<?, DiscountsFragment> fragmentTestRule =
            FragmentTestRule.create(DiscountsFragment.class);


}
