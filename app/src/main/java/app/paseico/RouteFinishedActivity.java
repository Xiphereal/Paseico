package app.paseico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.User;

public class RouteFinishedActivity extends AppCompatActivity {

    Intent intent;
    String nombreRuta;
    List<PointOfInterest> nombrePOIs;
    int rewpoints;
    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference
    private User user = new User();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser fbusr = firebaseAuth.getCurrentUser();
    private DatabaseReference myActualUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //goToMenu.setClickable(false);
        Route route = (Route) getIntent().getExtras().get("route");
        myActualUserRef = myUsersRef.child(fbusr.getUid());
        myActualUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                int points = route.getRewardPoints();
                int actualpoints = user.getPoints();
                int updatedpoints = points + actualpoints;
                final DatabaseReference mypointsreference = myActualUserRef.child("points");
                mypointsreference.setValue(updatedpoints);
                //goToMenu.setClickable(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finished);

        TextView nombreRutaTV = findViewById(R.id.textViewTemporalMenuName);
        TextView listaPOISTV = findViewById(R.id.textViewTemporalMenuPOIS);
        TextView cantPuntos = findViewById(R.id.textViewRouteFinishedPoints);

        nombreRuta = route.getName();
        if (nombreRuta != null){nombreRutaTV.setText(nombreRuta);}

        rewpoints = route.getRewardPoints();
        cantPuntos.setText("Puntos: " + rewpoints);

        nombrePOIs = route.getPointsOfInterest();
        String listado = "";
        for (int i = 0; i < nombrePOIs.size(); i++) {
            listado+="- "+nombrePOIs.get(i).getName() + "\n";
        }

        listaPOISTV.setText(listado);
        System.out.println(route.isOrdered());




        //listaPOIS.setText(listado);
        Button goToMenu = findViewById(R.id.buttonTemporalMenuStartRoute);
        goToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteFinishedActivity.this, RouteInformationActivity.class);
                intent.putExtra("route",route);
                startActivity(intent);
                finish();
            }
        });
    }
}