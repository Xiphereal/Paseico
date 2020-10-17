package app.paseico;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.PointOfInterest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class CreateNewRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap createNewRouteMap;
    private List<PointOfInterest> pointsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);

        initializeMapFragment();


    }

    private void initializeMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.new_route_map);
        mapFragment.getMapAsync(this);
    }

    private void registerOnMarkerClickListener(){
        createNewRouteMap.setOnMarkerClickListener(marker -> {
            pointsSelected.add(new PointOfInterest(marker, marker.getId()));
            return true;
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        createNewRouteMap = googleMap;
        LatLng valenciaCathedral = new LatLng(39.47, -0.38);
        googleMap.addMarker(new MarkerOptions().position(valenciaCathedral).title("Cathedral"));
        LatLng albertosBar = new LatLng(39.47, -0.37);
        googleMap.addMarker(new MarkerOptions().position(albertosBar).title("Alberto's bar"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(valenciaCathedral));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        registerOnMarkerClickListener();
    }
}