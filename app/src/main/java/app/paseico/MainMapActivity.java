package app.paseico;

import android.content.Intent;
import android.view.View;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mainMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerCreateNewRouteButtonTransition();
    }

    private void registerCreateNewRouteButtonTransition() {
        ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.extended_fab);
        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewRouteIntent = new Intent(getApplicationContext(), CreateNewRouteActivity.class);
                startActivity(createNewRouteIntent);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mainMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng valenciaCathedral = new LatLng(39.47, -0.38);
        LatLng albertosBar = new LatLng(39.47,-0.37);
        mainMap.addMarker(new MarkerOptions().position(valenciaCathedral).title("Cathedral"));
        mainMap.addMarker(new MarkerOptions().position(albertosBar).title("Alberto's bar"));
        mainMap.moveCamera(CameraUpdateFactory.newLatLng(valenciaCathedral));
    }

    public static GoogleMap getMap(){
        return mainMap;
    }
}