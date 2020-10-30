package app.paseico;

import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.Route;

import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_information);

        Route route = (Route) getIntent().getExtras().get("route");

        textView_theme = findViewById(R.id.textView_routeInfo_theme);

    }
}