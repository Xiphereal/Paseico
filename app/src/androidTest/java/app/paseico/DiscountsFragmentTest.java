package app.paseico;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import app.paseico.data.Router;
import app.paseico.mainMenu.marketplace.DiscountsFragment;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

public class DiscountsFragmentTest {

    private static Router currentRouter;

    @Before
    public void setUpAll()  {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String email = "avd@gmail.com";
        String password = "123456";
        firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    @After
    public void resetPoints(){
        DatabaseReference myActualUserRef = FirebaseDatabase.getInstance().getReference("users")
                .child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("points");
        myActualUserRef.setValue(0);
    }

    @Rule
    public FragmentTestRule<DiscountsFragment> mFragmentTestRule = new FragmentTestRule<>(DiscountsFragment.class);

    @Test
    public void InstanciateListViewTest() {

        // Launch the activity to make the fragment visible
        mFragmentTestRule.launchActivity(null);

        // Then use Espresso to test the Fragment
        onView(withId(R.id.ListViewDiscounts)).check(matches(isDisplayed()));

    }

    @Test
    public void performClickTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(3000);
        
        onView(withId(R.id.ListViewDiscounts)).check(matches(isDisplayed()));

        onData(anything())
                .inAdapterView(withId(R.id.ListViewDiscounts))
                .atPosition(0).perform(click());
    }

    @Test
    public void performClickWithEnoughPointsTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        DatabaseReference myActualUserRef = FirebaseDatabase.getInstance().getReference("users")
                .child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("points");
        myActualUserRef.setValue(100000);

        Thread.sleep(3000);

        onView(withId(R.id.ListViewDiscounts)).check(matches(isDisplayed()));

        onData(anything())
                .inAdapterView(withId(R.id.ListViewDiscounts))
                .atPosition(0).perform(click());

        onView(withText("Enhorabuena! Acabas de canjear un descuento"))
                .inRoot(withDecorView(is(not(mFragmentTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void performClickWithoutEnoughPointsTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        DatabaseReference myActualUserRef = FirebaseDatabase.getInstance().getReference("users")
                .child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("points");
        myActualUserRef.setValue(0);

        Thread.sleep(3000);

        onView(withId(R.id.ListViewDiscounts)).check(matches(isDisplayed()));

        onData(anything())
                .inAdapterView(withId(R.id.ListViewDiscounts))
                .atPosition(0).perform(click());

        onView(withText("No tienes puntos suficientes para canjear el descuento"))
                .inRoot(withDecorView(is(not(mFragmentTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}

//ListView is an AdapterView. AdapterViews present a problem when doing UI testing. Since an AdapterView doesn't load all of it's items upfront (only as the user scrolls through the items), AdapterView's don't work well with onView(...) since the particular view might not be part of the view hierarchy yet.
//Fortunately, Espresso provides an onData(...) entry point that makes sure to load the AdapterView item before performing any operations on it.
//onData(instanceOf(DiscountObj.class)).atPosition(0);