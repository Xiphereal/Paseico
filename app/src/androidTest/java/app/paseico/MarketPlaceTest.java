package app.paseico;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;
import app.paseico.data.Router;
import app.paseico.mainMenu.marketplace.marketplaceFragment;

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

    @Rule
    public ActivityTestRule<LogInActivity> mActivityRule =
            new ActivityTestRule<>(LogInActivity.class);

    @Before
    public void setUpAll()  {
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String email = "avd@gmail.com";
        String password = "123456";
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mActivityRule.getActivity(), task ->{
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                currentRouter = new Router();
                FirebaseUser fbUser = firebaseAuth.getCurrentUser();

                DatabaseReference myActualUserRef = usersDatabaseReference.child(fbUser.getUid());
                myActualUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {// Get the actual user
                        currentRouter = snapshot.getValue(Router.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("The db connection failed");
                    }
                });

            } else {
                // If sign in fails, the test fails.
                fail();

            }
        });

        //User currentUser = databaseReference.child("7YuiqRHra8OaosZc9vpbfGXdy9C2").child("");
    }

    @Test
    public void buy1000PointsTest() {


        assertEquals(currentRouter.getUsername(), "avdtesting");

//        // Launch the activity to make the fragment visible
//        mFragmentTestRule.launchActivity(null);
//
//        // Then use Espresso to test the Fragment
//        onView(withId(R.id.imageViewBuy1000Points)).check(matches(isDisplayed()));
    }
}
