package app.paseico.mainMenu.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import app.paseico.mainMenu.searchUsers.FollowersActivity;
import app.paseico.R;
import app.paseico.data.Router;
import app.paseico.data.User;
import app.paseico.login.LogInActivity;
import app.paseico.mainMenu.searchUsers.UserAdapter;
import app.paseico.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {
    ImageView image_profile;
    TextView followers, textView_followers, following, textView_following, fullnameLabel, usernameLabel, userPointsLabel, numberOfUserRoutes;
    FirebaseUser firebaseUser;
    User actualUser;
    Button buttonLogOut;
    ImageButton userRoutes;
    private Boolean firstTimeCheckBoost = false;
    private Router user = new Router();
    private String usernameFirebase;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private int numberOfRoutes = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                actualUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        image_profile = view.findViewById(R.id.image_profile);
        followers = view.findViewById(R.id.textView_followersNumber);
        following = view.findViewById(R.id.textView_followingNumber);
        usernameLabel = view.findViewById(R.id.username);
        fullnameLabel = view.findViewById(R.id.fullname);
        buttonLogOut = view.findViewById(R.id.buttonLogOut);
        textView_followers = view.findViewById(R.id.textView_FollowersText);
        textView_following = view.findViewById(R.id.textView_FollowingText);
        userPointsLabel = view.findViewById(R.id.userPointsProfileText);
        numberOfUserRoutes = view.findViewById(R.id.numberOfRoutesText);
        userRoutes = view.findViewById(R.id.btn_my_routes);

        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //mUsers.add(user);
                //mUsers.clear();
                usernameFirebase = user.getUsername();
                //userAdapter.notifyDataSetChanged();
                setUserInfoOnGetCurrentUserReady();
                getFollowers();
                //if (profileid.equals(usernameFirebase)) { //HERE
                buttonLogOut.setText("Cerrar sesión");
                setButtonLogOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //getGetUsernameFromFirebase(profileid);

        followers.setOnClickListener(followersView -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", actualUser.getUsername());
            intent.putExtra("title", "followers");
            startActivity(intent);
        });

        textView_followers.setOnClickListener(followersTextViewView -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", actualUser.getUsername());
            intent.putExtra("title", "followers");
            startActivity(intent);
        });

        following.setOnClickListener(followingView -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", actualUser.getUsername());
            intent.putExtra("title", "following");
            startActivity(intent);
        });

        textView_following.setOnClickListener(followingTextViewView -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", actualUser.getUsername());
            intent.putExtra("title", "following");
            startActivity(intent);
        });

        userRoutes.setOnClickListener(v -> {
            NavDirections action = ProfileFragmentDirections.actionProfileToUserRoutesFragment();
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(action);
        });

        return view;
    }

    private void setUserInfoOnGetCurrentUserReady() {
        DatabaseReference currentUser = FirebaseService.getCurrentUserReference();

        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (getContext() == null) {
                    return;
                }
                Router user = snapshot.getValue(Router.class);
                //Glide.with(getContext()).load("@drawable/defaultProfilePic").into(image_profile);

                usernameLabel.setText(user.getUsername());
                fullnameLabel.setText(user.getName());
                userPointsLabel.setText(Integer.toString(user.getPoints()));

                setNumberOfAuthoredRoutes(firebaseUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setNumberOfAuthoredRoutes(String userUid) {
        numberOfRoutes = 0;
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference routesReference = database.collection("route");
        routesReference.whereEqualTo("authorId", userUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot ignored : task.getResult()) {
                            numberOfRoutes++;
                        }
                        numberOfUserRoutes.setText(Integer.toString(numberOfRoutes));
                    }
                });
        System.out.println(userUid + " userUID");
        System.out.println(numberOfRoutes + " jasdhfjkashdkjfhasdjhfkajhfkajdhkas");
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername()).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(actualUser.getUsername()).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //TODO: This is from the previous version idk if is needed on this one
    public void checkBoost() {  //Check if the boost its already gone
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fbusr = firebaseAuth.getCurrentUser();
        DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference myActualUserRef = myUsersRef.child(fbusr.getUid());

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (user.getBoostExpires() != null) {
            String strBoostDateEnding = user.getBoostExpires();
            Date actualDate = new Date();
            Date boostDateEnding = null;
            try {
                boostDateEnding = dateFormat.parse(strBoostDateEnding);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (actualDate.compareTo(boostDateEnding) >= 0) {
                Toast.makeText(getActivity(), "Tu boost ha caducado!", Toast.LENGTH_SHORT).show();
                final DatabaseReference myBoostReference = myActualUserRef.child("boost");
                myBoostReference.setValue(false);
            } else {
                Toast.makeText(getActivity(), "Tienes un boost activo que caducará el día :" + user.getBoostExpires(), Toast.LENGTH_SHORT).show();
            }
        }

        myActualUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Router.class);
                if (user.isBoost() && !firstTimeCheckBoost) {
                    checkBoost();
                    firstTimeCheckBoost = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The db connection failed");
            }
        });
    }

    private void setButtonLogOut() {
        buttonLogOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }
}

