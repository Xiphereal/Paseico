package app.paseico.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.paseico.MainMenuActivity;
import app.paseico.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import app.paseico.data.UserDao;

public class LogInActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;
    private FirebaseAuth firebaseAuth;
    private SignInButton googleSignInButton;
    private EditText etEmail, etPassword;
    private GoogleSignInClient googleSignInClient;
    private UserDao userDao = new UserDao();
    private Button routerBtn, organiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);




        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.buttonLogIn);

        btnLogin.setOnClickListener(view -> SignInPaseico());

        googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(v -> SignInGoogle());

        TextView register = findViewById(R.id.textViewRegister);

        //On click you go to the register form
        register.setOnClickListener(view -> {
            Intent intent = new Intent();
            if(!routerBtn.isEnabled()) {
                 intent = new Intent(LogInActivity.this, RegisterActivity.class);
            }
            else{
                 intent = new Intent(LogInActivity.this, RegisterOrganizationActivity.class);
            }
            startActivity(intent);
            finish();
        });

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        routerBtn = findViewById(R.id.buttonLoginRouter);
        organiBtn = findViewById(R.id.buttonLoginOrganization);
        routerBtn.setEnabled(false);
        organiBtn.setEnabled(true);

        routerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInButton.setVisibility(View.VISIBLE);
                routerBtn.setEnabled(false);
                organiBtn.setEnabled(true);
            }
        });

        organiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInButton.setVisibility(View.GONE);
                routerBtn.setEnabled(true);
                organiBtn.setEnabled(false);
            }
        });
    }

    private void SignInGoogle() {
        Intent signIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);
    }

    private void SignInPaseico() {
        firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success");
                        goToMainScreen();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LogInActivity.this, "Correo electronico o contraseña incorrectos!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null)
                    firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseAuthCurrentUser = firebaseAuth.getCurrentUser();
                        Log.d("TAG", "Signin success");

                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                        if (isNewUser)
                            registerNewUserToDatabase(account, task, firebaseAuthCurrentUser);
                        else
                            goToMainScreen();

                    } else {
                        Log.w("TAG", "Signin failed", task.getException());

                        Toast.makeText(this, "SingIn Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerNewUserToDatabase(GoogleSignInAccount account, Task<AuthResult> task, FirebaseUser user) {
        Log.w("TAG", "NEW USER", task.getException());
        Log.w("TAG", user.getUid(), task.getException());

        //Wait 2 secs to load the next activity (LoginScreen)
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Log.w("TAG", "GOOOOOOOOOOOOGLE", task.getException());
            userDao.addGoogleUser(user, account.getDisplayName());
            goToMainScreen();
        }, 3000);
    }



    private void goToMainScreen() {
        Intent intent = new Intent(LogInActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}