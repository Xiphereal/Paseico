package app.paseico;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import app.paseico.data.Route;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private ListView createdRoutesListView;
    private ArrayAdapter<String> createdRoutesListViewAdapter;
    private static List<String> createdRoutes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        createdRoutesListView = findViewById(R.id.createdRoutes_listView);

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
        ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.create_new_route_button);
        extendedFloatingActionButton.setOnClickListener(view -> {
            Intent createNewRouteIntent = new Intent(getApplicationContext(), CreateNewRouteActivity.class);
            startActivity(createNewRouteIntent);
        });

        updateCreatedRoutesListView();
    }

    private void updateCreatedRoutesListView(){
        createdRoutesListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,createdRoutes);
        createdRoutesListView.setAdapter(createdRoutesListViewAdapter);
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

    public static List<String> getCreatedRoutes(){
        return createdRoutes;
    }
}