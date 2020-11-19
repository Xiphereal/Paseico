package app.paseico.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import app.paseico.R;
import app.paseico.data.Organization;
import app.paseico.data.Router;
import app.paseico.data.UserDao;

public class RegisterOrganizationActivity extends AppCompatActivity {
 private EditText etName, etPass, etPassConf, etNif, etMail;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    String name = null;
    String nif = null;
    String email = null;
    String password = null;
    String passwordConf = null;
    private DatabaseReference myOrganizationsRef = FirebaseDatabase.getInstance().getReference("organizations"); //Node organizations reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_organization);

        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.editTextOrganiName);
        etPass = findViewById(R.id.editTextOrganiPass);
        etPassConf = findViewById(R.id.editTextOrganiPassConf);
        etNif = findViewById(R.id.editTextOrganiNif);
        etMail = findViewById(R.id.editTextOrganiMail);

        btnRegister = findViewById(R.id.buttonOrganiRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRegister();
            }
        });
    }

    private void checkRegister(){

        name = etName.getText().toString();
        email = etMail.getText().toString();
        password = etPass.getText().toString();
        passwordConf = etPassConf.getText().toString();
        nif = etNif.getText().toString();

        if(!TextUtils.isEmpty(etName.getText().toString())
                &&!TextUtils.isEmpty(etMail.getText().toString())
                &&!TextUtils.isEmpty(etPass.getText().toString())
                &&!TextUtils.isEmpty(etPassConf.getText().toString())
                &&!TextUtils.isEmpty(etNif.getText().toString())
        ){ //Check that the fields aren't empty
            if(password.length()>=6) {
                if (password.equals(passwordConf)) { //Check if passwords match
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userNameRef = ref.child("organizations");
                    Query queries = userNameRef.orderByChild("name").equalTo(name);
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains(".]") ){
                                Toast.makeText(RegisterOrganizationActivity.this, "Error: No puedes usar un nombre con los siguientes caracteres: '.'  '# ' '$'  '['  ']' ",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                if (!dataSnapshot.exists()) {
                                    //create new user
                                    submitRegister(name, nif, email, password);
                                } else {
                                    Context context = getApplicationContext();
                                    CharSequence text = "ERROR: La organización ya existe";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    };
                    queries.addListenerForSingleValueEvent(eventListener);
                } else { //Passwords doesn't match
                    Context context = getApplicationContext();
                    CharSequence text = "Las contraseñas no coinciden";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }else{
                Context context = getApplicationContext();
                CharSequence text = "La contraseña debe contener mínimo 6 caracteres";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        } else { //Empty fields
            Context context = getApplicationContext();
            CharSequence text = "Por favor, rellene todos los campos para poder registrarse";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private void submitRegister(String name, String nif, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Organization newOrganization = new Organization(name, email, name, nif);
                            myOrganizationsRef.child(user.getUid()).setValue(newOrganization);
                            Toast.makeText(RegisterOrganizationActivity.this, "Registro completado!",
                                    Toast.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() { //Wait 2 secs to load the next activity (LoginScreen)
                                @Override
                                public void run() {
                                    try {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(RegisterOrganizationActivity.this, LogInActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } catch (Exception e) {
                                    }
                                }
                            }, 2000);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterOrganizationActivity.this, "Error: El correo electrónico ya existe",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }


                });
    }
}