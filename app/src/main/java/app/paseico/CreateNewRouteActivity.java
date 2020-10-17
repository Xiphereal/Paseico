package app.paseico;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.PointOfInterest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class CreateNewRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap createNewRouteMap;
    private List<PointOfInterest> pointsSelected = new ArrayList<>();

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

    private void registerOnMarkerClickListener() {
        createNewRouteMap.setOnMarkerClickListener(marker -> {

            PointOfInterest poi = findClickedPointOfInterest(marker);

            if (isPointOfInterestSelected(poi)) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                pointsSelected.remove(poi);
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                pointsSelected.add(new PointOfInterest(marker, marker.getId()));
            }

            return true;
        });
    }

    private PointOfInterest findClickedPointOfInterest(Marker marker) {
        for (PointOfInterest poi : pointsSelected) {
            if (poi.getGoogleMarker().equals(marker))
                return poi;
        }

        return null;
    }

    private boolean isPointOfInterestSelected(PointOfInterest poi) {
        return poi != null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        createNewRouteMap = googleMap;
        addFakePOIsToMap(createNewRouteMap);
        LatLng fakeUserPosition = new LatLng(39.475,-0.375);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(fakeUserPosition));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        registerOnMarkerClickListener();
    }

    private void addFakePOIsToMap(GoogleMap googleMap) {
        LatLng valenciaCathedral = new LatLng(39.475139, -0.375372);
        googleMap.addMarker(new MarkerOptions().position(valenciaCathedral).title("Cathedral"));
        LatLng albertosBar = new LatLng(39.471958, -0.370947);
        googleMap.addMarker(new MarkerOptions().position(albertosBar).title("Alberto's bar"));
        LatLng torresSerrano = new LatLng(39.479063, -0.376115);
        googleMap.addMarker(new MarkerOptions().position(torresSerrano).title("Torres de Serrano"));
        LatLng ayuntamiento = new LatLng(39.469734, -0.376868);
        googleMap.addMarker(new MarkerOptions().position(ayuntamiento).title("Ayuntamiento"));
        LatLng gulliver = new LatLng(39.462987, -0.359719);
        googleMap.addMarker(new MarkerOptions().position(gulliver).title("Gulliver"));
    }
}