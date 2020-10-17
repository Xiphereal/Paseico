package app.paseico;

import android.content.Intent;
import android.view.View;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        initializeMapFragment();

        registerCreateNewRouteButtonTransition();
    }

    private void initializeMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);
    }

    private void registerCreateNewRouteButtonTransition() {
        ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.extended_fab);
        extendedFloatingActionButton.setOnClickListener(view -> {
            Intent createNewRouteIntent = new Intent(getApplicationContext(), CreateNewRouteActivity.class);
            startActivity(createNewRouteIntent);
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
        LatLng valenciaCathedral = new LatLng(39.47, -0.38);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(valenciaCathedral));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}