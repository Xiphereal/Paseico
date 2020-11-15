package app.paseico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.login.OpeningActivity;

public class RouteFinishedActivity extends AppCompatActivity {

    Intent intent;
    String nombreRuta;
    List<PointOfInterest> nombrePOIs;
    int rewpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Route route = (Route) getIntent().getExtras().get("route");
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
        System.out.println(nombreRuta + " " + listado);




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