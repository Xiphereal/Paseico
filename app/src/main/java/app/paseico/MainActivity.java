package app.paseico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        TextView prueba = findViewById(R.id.textView);
//        prueba.setText("Bienvenido a paseico!");

//        Button btnLogOut = findViewById(R.id.buttonLogOut);
//        btnLogOut.setOnClickListener(new View.OnClickListener() {
//           @Override
//            public void onClick(View view) {
//               FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
}