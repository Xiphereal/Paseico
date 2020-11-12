package app.paseico;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.User;
import app.paseico.mainMenu.userCreatedRoutes.UserCreatedRoutesFragment;
import app.paseico.service.FirebaseService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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

    private List<String> createdMarkers = new ArrayList<>();

    private User currentUser;

    private Marker userMarkerInCreation;

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
            showRouteCreationSummaryDialog();
        }
    }

    private void showRouteCreationSummaryDialog() {
        int routeCost = calculateRouteCost();

        String dialogMessage = getResources().getString(R.string.route_creation_summary_message, routeCost);

        AlertDialog.Builder builder = setUpBuilder(dialogMessage);

        builder.setOnDismissListener(dialog -> {
            int currentUserPoints = currentUser.getPoints();

            if (currentUserPoints >= routeCost) {
                currentUser.setPoints(currentUserPoints - routeCost);
                showConfirmationDialog();
            } else {
                showNotEnoughPointsDialog();
            }
        });

        showDialog(builder);
    }

    private int calculateRouteCost() {
        // TODO: When both Google Maps & User created Points Of Interest are supported, we must calculate the Route
        //  cost having their different costs in mind.
        return selectedPointsOfInterest.size() * getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost);
    }

    private void showDialog(AlertDialog.Builder builder) {
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConfirmationDialog() {
        String dialogMessage = getResources().getString(R.string.route_creation_confirmation_message);
        AlertDialog.Builder builder = setUpBuilder(dialogMessage);

        // In case the user close the dialog either by tapping outside of the dialog or by pressing any button,
        // it's considered dismissed.
        builder.setOnDismissListener(dialog -> finalizeRouteCreation());

        showDialog(builder);
    }

    /**
     * Sets up a basic builder for an AlertDialog. The caller must ensures the setOnDismissListener is defined
     * with the desired behavior for when closing the dialog.
     *
     * @param dialogMessage The String from resources can be retrieved by 'getResources().getString()'. This allows
     *                      to use formatted Strings for dynamic messages.
     * @return The setted up builder for the AlertDialog.
     */
    @NotNull
    private AlertDialog.Builder setUpBuilder(String dialogMessage) {
        // Where the alert dialog is going to be shown.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(dialogMessage)
                .setTitle(R.string.route_creation_finalize_title)
                .setPositiveButton("OK", (dialog, which) -> {
                    // This remains empty because when the dialog is closed by tapping on 'OK' or outside it,
                    // it's considered to be dismissed in both cases, thus the call to the finalizer method must
                    // be done only on the dismiss listener.
                });

        return builder;
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

    private void showNotEnoughPointsDialog() {
        String dialogMessage = getResources().getString(R.string.route_creation_not_enough_points_message);
        AlertDialog.Builder builder = setUpBuilder(dialogMessage);

        builder.setOnDismissListener(dialog -> {
            // This remains empty because we want the app to do nothing in this case.
        });

        showDialog(builder);
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
        registerOnMapLongClick();
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
            PointOfInterest poi = findClickedPointOfInterest(marker.getPosition(), marker.getTitle());

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
            PointOfInterest poi = findClickedPointOfInterest(poiSelected.latLng, poiSelected.name);


            if (!isPointOfInterestSelected(poi) && markerWasNotCreated(poiSelected.name)) {
                Marker markerOfThePoi = createNewRouteMap.addMarker(new MarkerOptions().position(poiSelected.latLng).title(poiSelected.name));
                selectPointOfInterest(markerOfThePoi);
                createdMarkers.add(poiSelected.name);
            }

            updateMarkedPOIsListView();

            return;
        });
    }

    private boolean markerWasNotCreated(String name) {
        for (String createdMarkerName : createdMarkers) {
            if (createdMarkerName.equals(name)) {
                return false;
            }
        }
        return true;
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

    private PointOfInterest findClickedPointOfInterest(LatLng latLangMarker, String title) {
        Double lat = latLangMarker.latitude;
        Double lon = latLangMarker.longitude;
        PointOfInterest markerPOI = new PointOfInterest(lat, lon, title);
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

    /**
     * Registers the listener for letting the user create Points Of Interest.
     */
    private void registerOnMapLongClick() {
        BottomSheetBehavior bottomSheetBehavior = initializeUserNewPoiForm();

        registerOnNewPoiButtonClicked(bottomSheetBehavior);

        createNewRouteMap.setOnMapLongClickListener(tapPoint -> {
            // Opens the creation form.
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            userMarkerInCreation = createNewRouteMap
                    .addMarker(new MarkerOptions().position(tapPoint).title("User Marker"));
        });
    }

    /**
     * Hides the bottom sheet containing the new Point Of Interest creation form.
     */
    @NotNull
    private BottomSheetBehavior initializeUserNewPoiForm() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior
                .from(findViewById(R.id.user_created_marker_bottom_sheet));

        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return bottomSheetBehavior;
    }

    /**
     * Registers the listener for accepting the new Point of Interest creation.
     *
     * @param bottomSheetBehavior The reference to the bottom sheet for hiding it.
     */
    private void registerOnNewPoiButtonClicked(BottomSheetBehavior bottomSheetBehavior) {
        Button createNewPointOfInterestButton = findViewById(R.id.user_created_marker_button);

        createNewPointOfInterestButton.setOnClickListener(button -> {
            TextInputEditText textInputEditText = findViewById(R.id.user_created_marker_name_text_input);

            userMarkerInCreation.setTitle(textInputEditText.getText().toString());

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    public static Route getRoute() {
        return newRoute;
    }
}