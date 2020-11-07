package app.paseico.ui.searchUsers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

//import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Thread.*;
import java.util.List;


import app.paseico.R;
import app.paseico.data.User;
import app.paseico.login.LogInActivity;

import static java.lang.Thread.sleep;

public class ProfileFragment extends Fragment {
    ImageView image_profile;
    TextView followers, following, fullname, username;
    FirebaseUser firebaseUser;
    String profileid;
    Button buttonLogOut;
    private Boolean firstTimeCheckBoost = false;
    private User user = new User();
    private String usernameFirebase;
    private UserAdapter userAdapter;
    private List<User> mUsers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //mUsers = new ArrayList<>();
        //userAdapter = new UserAdapter(getContext(),mUsers);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        buttonLogOut = view.findViewById(R.id.buttonLogOut);


        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //mUsers.add(user);
                //mUsers.clear();
                usernameFirebase = user.getUsername();
                //userAdapter.notifyDataSetChanged();
               // System.out.println(profileid + "+++++++++++++" + usernameFirebase + "+++++++++++++++++++++++++++++++++++++++++++++++++");
                userInfo();
                getFollowers();
              //  System.out.println(profileid + "----------" + usernameFirebase + "---------------------------------------------");
                //if (profileid.equals(usernameFirebase)) { //HERE
                    buttonLogOut.setText("Cerrar sesion");
                //} else {
                //    checkFollow();
                //}
                setButtonLogOut();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //getGetUsernameFromFirebase(profileid);




        return view;
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                //Glide.with(getContext()).load("@drawable/defaultProfilePic").into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    buttonLogOut.setText("following");
                } else {
                    buttonLogOut.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");
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
                .child("Follow").child(profileid).child("following");
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

    //ATTENTION: This is from the previous version idk if is needed on this one
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
                user = snapshot.getValue(User.class);
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

    /*private void getGetUsernameFromFirebase(String s){
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mUsers.add(user);
                        usernameFirebase = user.getUsername();
                        userAdapter.notifyDataSetChanged();
                    System.out.println(profileid + "+++++++++++++" + usernameFirebase + "+++++++++++++++++++++++++++++++++++++++++++++++++");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }*/

    private void setButtonLogOut(){
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = buttonLogOut.getText().toString();

                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                 /*else if (btn.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                } else if (btn.equals("following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }*/
            }
        });
    }
}

