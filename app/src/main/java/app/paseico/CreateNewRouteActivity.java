package app.paseico;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CreateNewRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap createNewRouteMap;
    private List<PointOfInterest> selectedPointsOfInterest = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);

        initializeMapFragment();

        registerFinalizeRouteCreationButtonTransition();
    }

    private void initializeMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.new_route_map);
        mapFragment.getMapAsync(this);
    }

    private void registerFinalizeRouteCreationButtonTransition() {
        ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.finalize_route_creation_button);
        extendedFloatingActionButton.setOnClickListener(view -> tryFinalizeRouteCreation());
    }

    private void tryFinalizeRouteCreation() {
        TextInputEditText textInputEditText = findViewById(R.id.route_name_textInputEditText);

        // TODO: Store the route in Firebase.
        Route newRoute = new Route(textInputEditText.getText().toString(), selectedPointsOfInterest);

        // TODO: When the conditions are not met, we must show an error message and just close the dialog on click OK.
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        // Where the alert dialog is going to be shown.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // TODO: Extract the string to a resources file or similar abstraction.
        builder.setMessage("La nueva ruta ha sido guardada satisfactoriamente.")
                .setTitle("Finalizar creación de ruta")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Take the user back to the main map activity
                    Intent goToMainMapIntent = new Intent(getApplicationContext(), MainMapActivity.class);
                    startActivity(goToMainMapIntent);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        createNewRouteMap = googleMap;

        addFakePOIsToMap(createNewRouteMap);

        LatLng fakeUserPosition = new LatLng(39.475, -0.375);
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

    private void registerOnMarkerClickListener() {
        createNewRouteMap.setOnMarkerClickListener(marker -> {
            PointOfInterest poi = findClickedPointOfInterest(marker);

            if (isPointOfInterestSelected(poi)) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                selectedPointsOfInterest.remove(poi);
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                selectedPointsOfInterest.add(new PointOfInterest(marker, marker.getId()));
            }

            return true;
        });
    }

    private PointOfInterest findClickedPointOfInterest(Marker marker) {
        for (PointOfInterest poi : selectedPointsOfInterest) {
            if (poi.getGoogleMarker().equals(marker))
                return poi;
        }

        return null;
    }

    private boolean isPointOfInterestSelected(PointOfInterest poi) {
        return poi != null;
    }
}