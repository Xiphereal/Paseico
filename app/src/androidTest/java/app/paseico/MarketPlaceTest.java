package app.paseico;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;
import app.paseico.data.Router;
import app.paseico.mainMenu.marketplace.marketplaceFragment;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import java.util.concurrent.Executor;

import app.paseico.data.Router;
import app.paseico.data.User;
import app.paseico.login.LogInActivity;
import app.paseico.mainMenu.marketplace.DiscountsFragment;
import app.paseico.mainMenu.marketplace.marketplaceFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MarketPlaceTest {
    private static Router currentRouter;
    //private FirebaseAuth firebaseAuth;
    //private static FirebaseUser fbUser;
    //private static DatabaseReference usersDatabaseReference;

    @Rule
    public FragmentTestRule<marketplaceFragment> mFragmentTestRule = new FragmentTestRule<>(marketplaceFragment.class);


    @Before
    public void setUpAll()  {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Login a created user which no have rewardsPoints.
        String email = "avd@gmail.com";
        String password = "123456";
        firebaseAuth.signInWithEmailAndPassword(email, password);
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


    @After
    public void resetPoints(){
        // Reset the rewardPoints of the user used to test this Fragment.
        DatabaseReference  myActualUserRef = FirebaseDatabase.getInstance().getReference("users").child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("points");
        myActualUserRef.setValue(0);
    }
}
