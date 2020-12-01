package app.paseico;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import app.paseico.mainMenu.marketplace.marketplaceFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

public class MarketplaceFragmentTest {

    @Rule
    public FragmentTestRule<marketplaceFragment> mFragmentTestRule =
            new FragmentTestRule<>(marketplaceFragment.class);

    @Before
    public void setUpAll()  {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Login a created user.
        String email = "avd@gmail.com";
        String password = "123456";
        firebaseAuth.signInWithEmailAndPassword(email, password);
        // Set to zero the rewards points of the logged user.
        DatabaseReference  myActualUserRef = FirebaseDatabase.getInstance().getReference("users")
                .child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("points");
        myActualUserRef.setValue(0);
        //Set to false the boost of the logged user.
        DatabaseReference  myActualUserRef2 = FirebaseDatabase.getInstance().getReference("users")
                .child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("boost");
        myActualUserRef2.setValue(false);
    }

    @AfterClass
    public static void resetPoints(){
        // Reset the rewardPoints of the user used to test this Fragment.
        DatabaseReference  myActualUserRef = FirebaseDatabase.getInstance().getReference("users").
                child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("points");
        myActualUserRef.setValue(0);

        // Reset the boost of the user used to test this Fragment.
        DatabaseReference  myActualUserRef2 = FirebaseDatabase.getInstance().getReference("users")
                .child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("boost");
        myActualUserRef2.setValue(false);
    }


    @Test
    public void buy1000PointsTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 0")));
        onView(withId(R.id.imageViewBuy1000Points)).perform(click());
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 1000")));

    }

    @Test
    public void buy2000PointsTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 0")));
        onView(withId(R.id.imageViewBuy2000Points)).perform(click());
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 2000")));

    }

    @Test
    public void buy6000PointsTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 0")));
        onView(withId(R.id.imageViewBuy6000Points)).perform(click());
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 6000")));

    }

    @Test
    public void boostx2Test() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);

        onView(withId(R.id.imageViewBuy2000Points)).perform(click());
        onView(withId(R.id.imageViewBuyBoost2w)).perform(click());
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 0")));

    }

    @Test
    public void boostx2WithoutEnoughPointsTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);

        onView(withId(R.id.imageViewBuyBoost2w)).perform(click());
        onView(withText("No tienes puntos suficientes para comprar el BOOST!"))
                .inRoot(withDecorView(is(not(mFragmentTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void boostx2WithAnotherBoostActiveTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);

        onView(withId(R.id.imageViewBuy2000Points)).perform(click());
        onView(withId(R.id.imageViewBuyBoost2w)).perform(click());
        onView(withId(R.id.imageViewBuy2000Points)).perform(click());
        onView(withId(R.id.imageViewBuyBoost2w)).perform(click());
        onView(withText("No puedes comprar un BOOST! Ya tienes uno activo"))
                .inRoot(withDecorView(is(not(mFragmentTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void boostx5Test() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);

        onView(withId(R.id.imageViewBuy6000Points)).perform(click());
        onView(withId(R.id.imageViewBuyBoost5w)).perform(click());
        onView(withId(R.id.textViewMarketplacePTS)).check(matches(withText("Tus puntos: 0")));

    }

    @Test
    public void boostx5WithoutEnoughPointsTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);

        onView(withId(R.id.imageViewBuyBoost5w)).perform(click());
        onView(withText("No tienes puntos suficientes para comprar el BOOST!"))
                .inRoot(withDecorView(is(not(mFragmentTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void boostx5WithAnotherBoostActiveTest() throws InterruptedException {

        mFragmentTestRule.launchActivity(null);

        Thread.sleep(4500);

        onView(withId(R.id.imageViewBuy6000Points)).perform(click());
        onView(withId(R.id.imageViewBuyBoost5w)).perform(click());
        onView(withId(R.id.imageViewBuy6000Points)).perform(click());
        onView(withId(R.id.imageViewBuyBoost5w)).perform(click());
        onView(withText("No puedes comprar un BOOST! Ya tienes uno activo"))
                .inRoot(withDecorView(is(not(mFragmentTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

}
