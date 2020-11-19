package app.paseico.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.paseico.MainMenuActivity;
import app.paseico.MainMenuOrganizationActivity;
import app.paseico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OpeningActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { //Wait 2 secs to load the next activity (LoginScreen)
            @Override
            public void run() {
                try {
                    ProgressBar pBar = findViewById(R.id.progressBar);
                    pBar.setVisibility(View.GONE);
                    if (currentUser == null) {
                        Intent intent = new Intent(OpeningActivity.this, LogInActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
                        DatabaseReference mOrganizationsReference = FirebaseDatabase.getInstance().getReference("organizations").child(currentUser.getUid());

                        ValueEventListener userEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Intent intent = new Intent(OpeningActivity.this, MainMenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        };
                        ValueEventListener organiEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Intent intent = new Intent(OpeningActivity.this, MainMenuOrganizationActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        };
                        mUsersReference.addListenerForSingleValueEvent(userEventListener);
                        mOrganizationsReference.addListenerForSingleValueEvent(organiEventListener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }
}
