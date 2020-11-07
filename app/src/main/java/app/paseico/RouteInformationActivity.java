package app.paseico;

import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.Route;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class RouteInformationActivity extends AppCompatActivity {

    private TextView textView_name;
    private TextView textView_theme;
    private TextView textView_rewardsPoints;
    private TextView textView_length;
    private TextView textView_estimatedTime;
    private TextView textView_numberOfPOI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_information);

        textView_name = findViewById(R.id.textView_routeInfo_nameOfRoute);
        textView_theme = findViewById(R.id.textView_routeInfo_theme);
        textView_rewardsPoints = findViewById(R.id.textView_routeInfo_rewardPoints);
        textView_length = findViewById(R.id.textView_routeInfo_length);
        textView_estimatedTime = findViewById(R.id.textView_routeInfo_estimatedTime);
        textView_numberOfPOI = findViewById(R.id.textView_routeInfo_numberOfPOI);

        Route route = (Route) getIntent().getExtras().get("route");

        String name = route.getName();
        String theme = (route.getTheme() == null) ? "Sin temática" : route.getTheme();
        String rewardsPoints = ((Integer) route.getRewardPoints()).toString();
        int kms = (int) route.getLength() / 1000;
        int meters = (int) route.getLength() % 1000;
        String length = kms + " km y " + meters + " metros";
        int hours = ((int) route.getEstimatedTime()) / 60;
        int minutes = ((int) route.getEstimatedTime()) % 60;
        String estimatedTime = hours + " horas y " + minutes + " minutos" ;
        String numberOfPOI = route.getPointsOfInterest().size() + "";

        textView_name.setText(name);
        textView_theme.setText(theme);
        textView_rewardsPoints.setText(rewardsPoints);
        textView_length.setText(length);
        textView_estimatedTime.setText(estimatedTime);
        textView_numberOfPOI.setText(numberOfPOI);

        findViewById(R.id.btn_routeInfo_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_routeInfo_startRoute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startRouteIntent = new Intent(RouteInformationActivity.this, RouteRunnerActivity.class);
                startRouteIntent.putExtra("route", route);
                startActivity(startRouteIntent);
            }
        });

    }
}