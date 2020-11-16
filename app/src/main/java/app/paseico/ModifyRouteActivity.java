package app.paseico;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ModifyRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputLayout routeName;

    private List<PointOfInterest> pointsOfInterest = new ArrayList<>();
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.new_route_map);
        mapFragment.getMapAsync(this);

        Intent retrievedIntent = this.getIntent();
        Bundle retrievedData = retrievedIntent.getExtras();
        Route retrievedRoute = (Route) retrievedData.get("selectedRoute");

        routeName = findViewById(R.id.route_name_textField);
        routeName.setHint(retrievedRoute.getName());

        pointsOfInterest = retrievedRoute.getPointsOfInterest();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        for (int i = 0; i < pointsOfInterest.size(); i++) {
            Double latitude = pointsOfInterest.get(i).getLatitude();
            Double longitude = pointsOfInterest.get(i).getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(latLng));
        }
    }
}