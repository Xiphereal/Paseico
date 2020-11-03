package app.paseico.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import app.paseico.MainMenuActivity;
import app.paseico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                        Intent intent = new Intent(OpeningActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }
}
