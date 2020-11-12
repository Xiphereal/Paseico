package app.paseico;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.User;
import app.paseico.service.FirebaseService;
import app.paseico.mainMenu.userCreatedRoutes.UserCreatedRoutesFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreateNewRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap createNewRouteMap;
    private List<PointOfInterest> selectedPointsOfInterest = new ArrayList<>();
    private static Route newRoute;

    private ListView markedPOIsListView;
    private ArrayAdapter<String> markedPOIsAdapter;
    private List<String> markedPOIs = new ArrayList<>();

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);
        markedPOIsListView = findViewById(R.id.marked_pois_list_view);

        initializeMapFragment();

        getCurrentUserFromDatabaseAsync();
    }

    private void initializeMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.new_route_map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Gets the current User from the database asynchronously.
     */
    private void getCurrentUserFromDatabaseAsync() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentUserReference();

        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);

                // Registering this callback here ensures that the button
                // action is only performed when the User is ready.
                registerFinalizeRouteCreationButtonTransition();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The db connection failed: " + error.getMessage());
            }
        });
    }

    private void registerFinalizeRouteCreationButtonTransition() {
        ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.finalize_route_creation_button);

        // TODO: When the conditions are not met, we must show an error message and just close the dialog on click OK.
        extendedFloatingActionButton.setOnClickListener(view -> tryFinalizeRouteCreation());
    }

    /**
     * Checks for the User requirements for creating the new Route. If everything is fine, a confirmation dialog
     * appears and the Route creation finalizes. If anything goes wrong, a error dialogs appears and keeps the
     * previous state.
     */
    private void tryFinalizeRouteCreation() {
        if (currentUser.getHasFreeRouteCreation()) {
            currentUser.setHasFreeRouteCreation(false);
            showConfirmationDialog();
        } else {
            // TODO: Replace with the real in-creation Route cost.
            int routeCost = Integer.MAX_VALUE;
            int currentUserPoints = currentUser.getPoints();

            if (currentUserPoints >= routeCost) {
                currentUser.setPoints(currentUserPoints - routeCost);
                showConfirmationDialog();
            } else {
                // TODO: Show a similar dialog from showConfirmationDialog() but with an error message
                //  showing that the user doesn't have enough points.
            }
        }
    }

    private void showConfirmationDialog() {
        // Where the alert dialog is going to be shown.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // TODO: Extract the string to a resources file or similar abstraction.
        builder.setMessage("La nueva ruta ha sido guardada satisfactoriamente.")
                .setTitle("Finalizar creación de ruta")
                .setPositiveButton("OK", (dialog, which) -> finalizeRouteCreation());

        // In case the user taps out of the dialog thus dismissing it, the state must be consistent.
        builder.setOnDismissListener(dialog -> finalizeRouteCreation());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void finalizeRouteCreation() {
        createNewRoute();
        persistCurrentUserModifications();

        goToPreviousActivity();
    }

    private void createNewRoute() {
        TextInputEditText textInputEditText = findViewById(R.id.route_name_textInputEditText);
        String authorId = FirebaseService.getCurrentUser().getUid();

        newRoute = new Route(textInputEditText.getText().toString(), selectedPointsOfInterest, authorId);

        FirebaseService.saveRoute(newRoute);

        //We add the created route name to the createdRoutes before returning to the main activity.
        UserCreatedRoutesFragment.getCreatedRoutes().add(newRoute.getName());
    }

    // TODO: Refactor and generalize this into a User instance method.
    private void persistCurrentUserModifications() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentUserReference();

        currentUserReference.child("hasFreeRouteCreation").setValue(currentUser.getHasFreeRouteCreation());
        currentUserReference.child("points").setValue(currentUser.getPoints());
    }

    // TODO: Clean the current activity state to prevent the user retrieve the state when
    //  using the backstack.
    private void goToPreviousActivity() {
        Intent goToRoutesIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(goToRoutesIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        createNewRouteMap = googleMap;

        createNewRouteMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.create_route_style));

        addFakePOIsToMap(createNewRouteMap);

        // TODO: Move camera to real user position.
        LatLng fakeUserPosition = new LatLng(39.475, -0.375);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(fakeUserPosition));

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        registerOnMarkerClickListener();
        registerOnPOIClickListener();
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
                deselectPointOfInterest(marker, poi);
            } else {
                selectPointOfInterest(marker);
            }

            updateMarkedPOIsListView();

            return true;
        });
    }

    private void registerOnPOIClickListener() {
        createNewRouteMap.setOnPoiClickListener(poiSelected -> {
            // TODO: Uncomment. Commented so that the app can compile.
            //PointOfInterest poi = findClickedPointOfInterest(poiSelected.latLng.latitude, poiSelected.latLng.longitude, poiSelected.name);
        });
    }

    private void deselectPointOfInterest(@NotNull Marker marker, @NotNull PointOfInterest poi) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        markedPOIs.remove(poi.getName());

        selectedPointsOfInterest.remove(poi);
    }

    private void selectPointOfInterest(@NotNull Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        markedPOIs.add(marker.getTitle());

        selectedPointsOfInterest.add(new PointOfInterest(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle()));
    }

    private void updateMarkedPOIsListView() {
        markedPOIsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, markedPOIs);
        markedPOIsListView.setAdapter(markedPOIsAdapter);
    }

    private PointOfInterest findClickedPointOfInterest(@NotNull Marker marker) {
        LatLng latLangMarker = marker.getPosition();
        Double lat = latLangMarker.latitude;
        Double lon = latLangMarker.longitude;
        PointOfInterest markerPOI = new PointOfInterest(lat, lon, marker.getTitle());
        for (PointOfInterest poi : selectedPointsOfInterest) {
            if (poi.equals(markerPOI)) {
                return poi;
            }
        }

        return null;
    }

    private boolean isPointOfInterestSelected(PointOfInterest poi) {
        return poi != null;
    }

    public static Route getRoute() {
        return newRoute;
    }
}