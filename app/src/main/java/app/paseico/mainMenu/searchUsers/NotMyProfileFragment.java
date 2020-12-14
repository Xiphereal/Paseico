package app.paseico.mainMenu.searchUsers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.paseico.R;
import app.paseico.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;

public class NotMyProfileFragment extends Fragment {
    ImageView image_profile;
    TextView textView_followersNumber, textView_followersText, textView_followingText, textView_followingNumber, textView_fullname, textView_username;
    FirebaseUser firebaseUser;
    String profileid;
    User actualUser;
    User foreignUser;
    String searchedUser;
    Button buttonLogOut;
    private Boolean firstTimeCheckBoost = false;
    private User user = new User();
    private String usernameFirebase;
    private UserAdapter userAdapter;
    private List<User> mUsers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaanceState) {

        View view = inflater.inflate(R.layout.fragment_notmyprofile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                actualUser = dataSnapshot.getValue(User.class);
                FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user.getUsername().equals(profileid)) {
                                searchedUser = snapshot.getKey();
                                FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        usernameFirebase = user.getUsername();
                                        userInfo();
                                        getFollowers();
                                        checkFollow();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        image_profile = view.findViewById(R.id.image_profile);
        textView_followersNumber = view.findViewById(R.id.textView_followersNumber);
        textView_followingNumber = view.findViewById(R.id.textView_followingNumber);
        textView_username = view.findViewById(R.id.username);
        textView_fullname = view.findViewById(R.id.fullname);
        buttonLogOut = view.findViewById(R.id.buttonLogOut);
        textView_followersText = view.findViewById(R.id.textView_FollowersText);
        textView_followingText = view.findViewById(R.id.textView_FollowingText);


        buttonLogOut.setOnClickListener(v -> {
            String btn = buttonLogOut.getText().toString();
            if (btn.equals("Seguir")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername())
                        .child("following").child(profileid).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                        .child("followers").child(actualUser.getUsername()).setValue(true);
                buttonLogOut.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            } else if (btn.equals("Siguiendo")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername())
                        .child("following").child(profileid).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                        .child("followers").child(actualUser.getUsername()).removeValue();
                buttonLogOut.setBackground(getResources().getDrawable(R.drawable.gradient));
            }
        });


        return view;
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(searchedUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                foreignUser = user;
                textView_username.setText(user.getUsername());
                textView_fullname.setText(user.getName());
                activateFollowListeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    buttonLogOut.setText("Siguiendo");
                    buttonLogOut.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    buttonLogOut.setText("Seguir");
                    buttonLogOut.setBackground(getResources().getDrawable(R.drawable.gradient));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView_followersNumber.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView_followingNumber.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void activateFollowListeners() {
        textView_followersNumber.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", foreignUser.getUsername());
            intent.putExtra("title", "followers");
            startActivity(intent);
        });

        textView_followersText.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", foreignUser.getUsername());
            intent.putExtra("title", "followers");
            startActivity(intent);
        });

        textView_followingNumber.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", foreignUser.getUsername());
            intent.putExtra("title", "following");
            startActivity(intent);
        });

        textView_followingText.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", foreignUser.getUsername());
            intent.putExtra("title", "following");
            startActivity(intent);
        });
    }
}
