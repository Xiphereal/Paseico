package app.paseico;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.Router;
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
    private final List<PointOfInterest> selectedPointsOfInterest = new ArrayList<>();
    private static Route newRoute;

    private ListView markedPOIsListView;
    private final List<String> markedPOIs = new ArrayList<>();

    private final List<String> createdMarkers = new ArrayList<>();

    private Router currentUser;

    private Marker userNewCustomPoiInCreation;

    //int to know if the route is ordered or not
    int isOrdered=0;

    //Switch to set the route as ordered or not
    Switch orderedRouteSwitch;

    //buttons to change order
    Button poiUp;
    Button poiDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);
        markedPOIsListView = findViewById(R.id.marked_pois_list_view);

        poiUp = findViewById(R.id.poiUp_button);
        poiDown = findViewById(R.id.poiDown_button);
        registerOrderedRouteSwitch();

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
                currentUser = snapshot.getValue(Router.class);

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
        int totalRouteCost = 0;

        for (PointOfInterest poi : selectedPointsOfInterest) {
            if (poi.wasCreatedByUser()) {
                totalRouteCost += getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost);
            } else {
                totalRouteCost += getResources().getInteger(R.integer.google_maps_point_of_interest_cost);
            }
        }

        return totalRouteCost;
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
        newRoute.setOrdered(isOrdered);
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

        // TODO: Move camera to real user position.
        LatLng fakeUserPosition = new LatLng(39.475, -0.375);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(fakeUserPosition));

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        registerOnMapClick();
        registerOnMarkerClickListener();
        registerOnGoogleMapsPoiClickListener();
        registerOnMapLongClick();
    }

    private void registerOnMapClick() {
        createNewRouteMap.setOnMapClickListener(tapPoint -> tryDeleteUserNewCustomPoiInCreation());
    }

    /**
     * Checks if the User is creating a custom Point Of Interest.
     * If so, cancels the creation of the new POI.
     * If not, does nothing.
     */
    private void tryDeleteUserNewCustomPoiInCreation() {
        if (isUserCreatingACustomPoi()) {
            hideUserNewCustomPoiForm();
            userNewCustomPoiInCreation.remove();
            userNewCustomPoiInCreation = null;
        }
    }

    private boolean isUserCreatingACustomPoi() {
        return userNewCustomPoiInCreation != null;
    }

    private void registerOnMarkerClickListener() {
        createNewRouteMap.setOnMarkerClickListener(marker -> {

            /*Check if the marker selected is associated with the POI in creation
            * If it not, we continue as planned.
            * If it is, we do nothing.
            * */
            if (!marker.equals(userNewCustomPoiInCreation)) {
                tryDeleteUserNewCustomPoiInCreation();

                PointOfInterest poi = findClickedPointOfInterest(marker.getPosition(), marker.getTitle());

                if (isPointOfInterestSelected(poi)) {
                    deselectPointOfInterest(marker, poi);
                } else {
                    selectPointOfInterest(marker, false);
                }
            }

            return true;
        });
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

    private void deselectPointOfInterest(@NotNull Marker marker, @NotNull PointOfInterest poi) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        markedPOIs.remove(poi.getName());

        selectedPointsOfInterest.remove(poi);

        updateMarkedPOIsListView();
    }

    private void selectPointOfInterest(@NotNull Marker marker, boolean createdByUser) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        markedPOIs.add(marker.getTitle());

        PointOfInterest selectedPoi = new PointOfInterest(
                marker.getPosition().latitude,
                marker.getPosition().longitude,
                marker.getTitle(),
                createdByUser);

        selectedPointsOfInterest.add(selectedPoi);

        updateMarkedPOIsListView();
    }

    private void updateMarkedPOIsListView() {
        ArrayAdapter<String> markedPOIsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, markedPOIs);
        markedPOIsListView.setAdapter(markedPOIsAdapter);
    }

    /**
     * Registers the listener for creating a marker when a Google Maps Point of Interest is tapped.
     */
    private void registerOnGoogleMapsPoiClickListener() {
        createNewRouteMap.setOnPoiClickListener(poiSelected -> {
            tryDeleteUserNewCustomPoiInCreation();

            PointOfInterest poi = findClickedPointOfInterest(poiSelected.latLng, poiSelected.name);

            if (!isPointOfInterestSelected(poi) && !markerWasCreated(poiSelected.name)) {
                Marker markerOfThePoi = createNewRouteMap
                        .addMarker(new MarkerOptions().position(poiSelected.latLng).title(poiSelected.name));

                selectPointOfInterest(markerOfThePoi, false);
                createdMarkers.add(poiSelected.name);
            }

            return;
        });
    }

    private boolean markerWasCreated(String name) {
        for (String createdMarkerName : createdMarkers) {
            if (createdMarkerName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers the listener for letting the user create Points Of Interest.
     */
    private void registerOnMapLongClick() {
        BottomSheetBehavior bottomSheetBehavior = hideUserNewCustomPoiForm();

        registerOnNewPoiButtonClicked(bottomSheetBehavior);

        createNewRouteMap.setOnMapLongClickListener(tapPoint -> {
            tryDeleteUserNewCustomPoiInCreation();

            // Opens the creation form.
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            userNewCustomPoiInCreation = createNewRouteMap
                    .addMarker(new MarkerOptions().position(tapPoint).title("User Marker"));
        });
    }

    private void registerOrderedRouteSwitch(){
        orderedRouteSwitch = (Switch) findViewById(R.id.orderedRoute_switch);
        orderedRouteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    poiUp.setVisibility(View.VISIBLE);
                    poiUp.setClickable(true);
                    poiDown.setVisibility(View.VISIBLE);
                    poiDown.setClickable(true);
                    isOrdered=1;
                } else {
                    poiUp.setVisibility(View.INVISIBLE);
                    poiUp.setClickable(false);
                    poiDown.setVisibility(View.INVISIBLE);
                    poiDown.setClickable(false);
                    isOrdered=0;
                }
            }
        });
    }

    /**
     * Hides the bottom sheet containing the new Point Of Interest creation form.
     */
    @NotNull
    private BottomSheetBehavior hideUserNewCustomPoiForm() {
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

            userNewCustomPoiInCreation.setTitle(textInputEditText.getText().toString());
            textInputEditText.getText().clear();

            selectPointOfInterest(userNewCustomPoiInCreation, true);

            userNewCustomPoiInCreation = null;

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    public static Route getRoute() {
        return newRoute;
    }
}