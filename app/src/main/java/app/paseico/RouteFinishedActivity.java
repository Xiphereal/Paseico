package app.paseico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RouteFinishedActivity extends AppCompatActivity {

    Intent intent;
    String nombreRuta;
    ArrayList<String> nombrePOIs;
    int rewpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finished);

        TextView nombreRutaTV = findViewById(R.id.textViewTemporalMenuName);
        TextView listaPOISTV = findViewById(R.id.textViewTemporalMenuPOIS);
        TextView cantPuntos = findViewById(R.id.textViewRouteFinishedPoints);

        nombreRuta = intent.getStringExtra("nombreruta");
        if (nombreRuta != null){nombreRutaTV.setText(nombreRuta);}

        rewpoints = intent.getIntExtra("points", 0);
        cantPuntos.setText("Puntos: " + rewpoints);

        nombrePOIs = intent.getStringArrayListExtra("nombrePOIS");
        String listado = "";
        for (int i = 0; i < nombrePOIs.size(); i++) {
            listado+="- "+nombrePOIs.get(i) + "\n";
        }

        listaPOISTV.setText(listado);
        System.out.println(nombreRuta + " " + listado);




        //listaPOIS.setText(listado);

        Button goToMenu = findViewById(R.id.buttonTemporalMenuStartRoute);
        goToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteFinishedActivity.this, TemporalRoutesMenu.class);
                startActivity(intent);
                finish();
            }
        });
    }
}