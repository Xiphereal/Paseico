package app.paseico;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.Router;
import app.paseico.service.FirebaseService;
import app.paseico.utils.LocationPermissionRequester;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModifyRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap modifyRouteMap;
    private ListView markedPOIsListView;

    private List<PointOfInterest> pointsOfInterest = new ArrayList<>();
    private List<PointOfInterest> originalPOIs = new ArrayList<>();

    private Marker userNewCustomPoiInCreation;
    private final List<String> createdMarkers = new ArrayList<>();
    private final List<String> markedPOIs = new ArrayList<>();

    private List<PointOfInterest> newPointsOfInterest = new ArrayList<>();

    final double ROUTE_TOTAL_COST_MULTIPLIER_TO_GET_REWARD_POINTS = 0.5;

    //Switch to show or not the poi list and buttons to order it
    protected Switch showPOIsSwitch;

    //buttons to change order
    protected Button poiUpButton;
    protected Button poiDownButton;

    protected String selectedPOIinList = "";
    protected int positionOfPOIinList = 0;
    protected int nextPosition = 0;

    private Router currentRouter;
    private String retrievedRouteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_route);

        Bundle retrievedData = this.getIntent().getExtras();
        Route retrievedRoute = (Route) retrievedData.get("route");

        setUpRouteNameNonIntractableDisplay(retrievedRoute);

        pointsOfInterest = retrievedRoute.getPointsOfInterest();
        retrievedRouteId = getIntent().getStringExtra("routeID");

        registerMarkedPOIsListView();
        registerUpAndDownButtons();
        registerOrderedRouteSwitch();

        initializeMapFragment();

        getCurrentUserFromDatabaseAsync();
    }

    private void setUpRouteNameNonIntractableDisplay(Route retrievedRoute) {
        TextInputLayout routeName = findViewById(R.id.route_name_textField);
        routeName.setHint(retrievedRoute.getName());

        // Disables the edition.
        routeName.setEnabled(false);
    }

    private void initializeMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.new_route_map);
        mapFragment.getMapAsync(this);
    }

    private void registerMarkedPOIsListView() {
        markedPOIsListView = findViewById(R.id.marked_pois_list_view);
        markedPOIsListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPOIinList = (String) markedPOIsListView.getItemAtPosition(position);
            positionOfPOIinList = position;
        });
    }

    private void registerUpAndDownButtons() {
        poiUpButton = findViewById(R.id.poiUp_button_modify);
        poiUpButton.setOnClickListener(v -> moveUpSelectedPoiInList());

        poiDownButton = findViewById(R.id.poiDown_button_modify);
        poiDownButton.setOnClickListener(v -> moveDownSelectedPoiInList());
    }

    private void moveUpSelectedPoiInList() {
        if (!selectedPOIinList.equals("") && positionOfPOIinList != 0) {
            nextPosition = positionOfPOIinList - 1;
            moveSelectedPoiInList();
        } else {
            //Toast: select a poi of the list
            Toast.makeText(ModifyRouteActivity.this, "Selecciona un POI debajo de otro.", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveDownSelectedPoiInList() {
        if (!selectedPOIinList.equals("") && positionOfPOIinList != markedPOIs.size() - 1) {
            nextPosition = positionOfPOIinList + 1;
            moveSelectedPoiInList();
        } else {
            //Toast: select a poi of the list
            Toast.makeText(ModifyRouteActivity.this, "Selecciona un POI encima de otro", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveSelectedPoiInList() {
        markedPOIs.set(positionOfPOIinList, markedPOIs.get(nextPosition));
        markedPOIs.set(nextPosition, selectedPOIinList);

        PointOfInterest poiSelectedInListView = pointsOfInterest.get(positionOfPOIinList);
        pointsOfInterest.set(positionOfPOIinList, pointsOfInterest.get(nextPosition));
        pointsOfInterest.set(nextPosition, poiSelectedInListView);

        updateMarkedPOIsListView();
        selectedPOIinList = "";
    }

    private void registerOrderedRouteSwitch() {
        showPOIsSwitch = (Switch) findViewById(R.id.showPOIs_switch_modify);
        showPOIsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                poiUpButton.setVisibility(View.VISIBLE);
                poiUpButton.setClickable(true);
                poiDownButton.setVisibility(View.VISIBLE);
                poiDownButton.setClickable(true);
                markedPOIsListView.setVisibility(View.VISIBLE);
            } else {
                poiUpButton.setVisibility(View.INVISIBLE);
                poiUpButton.setClickable(false);
                poiDownButton.setVisibility(View.INVISIBLE);
                poiDownButton.setClickable(false);
                markedPOIsListView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        modifyRouteMap = googleMap;

        populateMapWithRoutePointsOfInterest();

        LocationPermissionRequester.requestLocationPermission(this);

        tryCenterCameraOnRoutePointsOfInterestGeometricCenter();

        registerOnMapClick();
        registerOnMarkerClickListener();
        registerOnGoogleMapsPoiClickListener();
        registerOnMapLongClick();

        updateMarkedPOIsListView();
    }

    private void populateMapWithRoutePointsOfInterest() {
        for (PointOfInterest pointOfInterest : pointsOfInterest) {
            LatLng position = new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude());
            String title = pointOfInterest.getName();

            Marker marker = modifyRouteMap.addMarker(new MarkerOptions().position(position).title(title));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            createdMarkers.add(pointOfInterest.getName());
            markedPOIs.add(pointOfInterest.getName());
            originalPOIs.add(pointOfInterest);
        }
    }

    /**
     * Try to move the camera to the {@link PointOfInterest} geometric
     * center if the coarse location permission are already granted.
     */
    private void tryCenterCameraOnRoutePointsOfInterestGeometricCenter() {
        if (LocationPermissionRequester.isCoarseLocationPermissionAlreadyGranted(this)) {
            centerCameraOnRoutePointsOfInterestGeometricCenter();
        }
    }

    private void centerCameraOnRoutePointsOfInterestGeometricCenter() {
        // Enables all the UI related to the user location and bearing.
        modifyRouteMap.setMyLocationEnabled(true);

        // The use of a deprecated method is due to convenience and ease of use.
        // Also, a similar approach has been taken in other parts of this codebase.
        modifyRouteMap.setOnMyLocationChangeListener(location -> {
            if (location == null) {
                return;
            }

            LatLngBounds pointsOfInterestBounds = calculatePointsBounds(pointsOfInterest);

            int cameraPaddingForBounds = 200;
            modifyRouteMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(pointsOfInterestBounds, cameraPaddingForBounds));
        });
    }

    /**
     * For the algorithm reference: https://stackoverflow.com/a/27601389
     */
    @NotNull
    private LatLngBounds calculatePointsBounds(List<PointOfInterest> pointOfInterests) {
        // First, the PointOfInterest list must be converted to plain positions.
        // In this case, a position is represented by a LatLng.
        List<LatLng> pointsOfInterestPositions =
                pointOfInterests.stream()
                        .map(poi -> new LatLng(poi.getLatitude(), poi.getLongitude()))
                        .collect(Collectors.toList());

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (LatLng position : pointsOfInterestPositions) {
            boundsBuilder.include(position);
        }

        return boundsBuilder.build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LocationPermissionRequester.LOCATION_REQUEST_CODE) {
            if (LocationPermissionRequester.didUserGrantCoarseLocationPermission(grantResults)) {
                centerCameraOnRoutePointsOfInterestGeometricCenter();
            }
        }
    }

    private void registerOnGoogleMapsPoiClickListener() {
        modifyRouteMap.setOnPoiClickListener(poiSelected -> {
            tryDeleteUserNewCustomPoiInCreation();

            PointOfInterest poi = findClickedPointOfInterest(poiSelected.latLng, poiSelected.name);

            if (!isPointOfInterestSelected(poi) && !markerWasCreated(poiSelected.name)) {
                Marker markerOfThePoi = modifyRouteMap
                        .addMarker(new MarkerOptions().position(poiSelected.latLng).title(poiSelected.name));

                selectPointOfInterest(markerOfThePoi, false);
                createdMarkers.add(poiSelected.name);
            }
        });
    }

    /**
     * Registers the listener for letting the user create Points Of Interest.
     */
    private void registerOnMapLongClick() {
        BottomSheetBehavior bottomSheetBehavior = hideUserNewCustomPoiForm();

        registerOnNewPoiButtonClicked(bottomSheetBehavior);

        modifyRouteMap.setOnMapLongClickListener(tapPoint -> {
            tryDeleteUserNewCustomPoiInCreation();

            // Opens the creation form.
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            userNewCustomPoiInCreation = modifyRouteMap
                    .addMarker(new MarkerOptions().position(tapPoint).title("User Marker"));
        });
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

    @NotNull
    private BottomSheetBehavior hideUserNewCustomPoiForm() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior
                .from(findViewById(R.id.user_created_marker_bottom_sheet));

        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return bottomSheetBehavior;
    }

    private PointOfInterest findClickedPointOfInterest(LatLng latLangMarker, String title) {
        Double lat = latLangMarker.latitude;
        Double lon = latLangMarker.longitude;
        PointOfInterest markerPOI = new PointOfInterest(lat, lon, title);

        for (PointOfInterest poi : pointsOfInterest) {
            if (poi.equals(markerPOI)) {
                return poi;
            }
        }

        return null;
    }

    private boolean isPointOfInterestSelected(PointOfInterest poi) {
        return poi != null;
    }

    private boolean markerWasCreated(String name) {
        for (String createdMarkerName : createdMarkers) {
            if (createdMarkerName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void deselectPointOfInterest(@NotNull Marker marker, @NotNull PointOfInterest poi) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        markedPOIs.remove(poi.getName());
        pointsOfInterest.remove(poi);
        newPointsOfInterest.remove(poi);

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

        pointsOfInterest.add(selectedPoi);

        newPointsOfInterest.add(selectedPoi);

        updateMarkedPOIsListView();
    }

    private void updateMarkedPOIsListView() {
        ArrayAdapter<String> markedPOIsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, markedPOIs);
        markedPOIsListView.setAdapter(markedPOIsAdapter);
    }

    private void registerOnMapClick() {
        modifyRouteMap.setOnMapClickListener(tapPoint -> tryDeleteUserNewCustomPoiInCreation());
    }

    private void registerOnMarkerClickListener() {
        modifyRouteMap.setOnMarkerClickListener(marker -> {
            if (!marker.equals(userNewCustomPoiInCreation)) {
                tryDeleteUserNewCustomPoiInCreation();

                PointOfInterest poi = findClickedPointOfInterest(marker.getPosition(), marker.getTitle());

                if (isPointOfInterestSelected(poi)) {
                    showConfirmationDeselection(marker, poi);
                } else {
                    selectPointOfInterest(marker, false);
                }
            }
            return true;
        });
    }

    private void showConfirmationDeselection(Marker marker, PointOfInterest poi) {
        String message = "¿Queres deseleccionar el punto?";
        AlertDialog.Builder builder = setUpBuilder(message);
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> deselectPointOfInterest(marker, poi));
        builder.setNegativeButton(android.R.string.no, (dialog, which) -> {
            // If the user chooses no, nothing is done.
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getCurrentUserFromDatabaseAsync() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentRouterReference();

        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentRouter = snapshot.getValue(Router.class);

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

    private void tryFinalizeRouteCreation() {
        if (pointsOfInterest.size()>=2) {
            if (currentRouter.getHasFreeRouteCreation()) {
                currentRouter.setHasFreeRouteCreation(false);
                showConfirmationDialog();
            } else {
                showRouteCreationSummaryDialog();
            }
        }else{
            Toast.makeText(ModifyRouteActivity.this, "Ruta debe tener 2 POIs mínimo", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRouteCreationSummaryDialog() {
        int routeCost = calculateRouteCost();

        String dialogMessage = getResources().getString(R.string.route_creation_summary_message, routeCost);

        AlertDialog.Builder builder = setUpBuilder(dialogMessage);
        builder.setCancelable(true);

        builder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> {
                    int currentUserPoints = currentRouter.getPoints();

                    if (currentUserPoints >= routeCost) {
                        currentRouter.setPoints(currentUserPoints - routeCost);
                        showConfirmationDialog();
                    } else {
                        showNotEnoughPointsDialog();
                    }
                });

        builder.setNegativeButton(android.R.string.no, (dialog, which) -> {
            // If the user chooses no, nothing is done.
        });

        showDialog(builder);
    }

    private int calculateRouteCost() {
        int totalRouteCost = 0;

        for (PointOfInterest poi : newPointsOfInterest) {
            if (!originalPOIs.contains(poi)) {
                if (poi.wasCreatedByUser()) {
                    totalRouteCost += getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_points);
                } else {
                    totalRouteCost += getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_points);
                }
            }
        }

        return totalRouteCost;
    }

    @NotNull
    private AlertDialog.Builder setUpBuilder(String dialogMessage) {
        // Where the alert dialog is going to be shown.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(dialogMessage)
                .setTitle(R.string.route_modification_finalize_title);

        return builder;
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
        builder.setPositiveButton("OK", (dialog, which) -> {
            // This remains empty because when the dialog is closed by tapping on 'OK' or outside it,
            // it's considered to be dismissed in both cases, thus the call to the finalizer method must
            // be done only on the dismiss listener.
        });

        showDialog(builder);
    }

    private void finalizeRouteCreation() {
        modifyRoute();
        persistCurrentUserModifications();

        goToPreviousActivity();
    }

    private void modifyRoute() {
        TextInputEditText textInputEditText = findViewById(R.id.route_name_textInputEditText);
        String authorId = FirebaseService.getCurrentUser().getUid();
        int rewardPointsGranted = calculateRouteRewardPoints();

        FirebaseService.updateRoute(retrievedRouteId, "rewardPoints", rewardPointsGranted);
        FirebaseService.updateRoute(retrievedRouteId, "pointsOfInterest", pointsOfInterest);
    }

    private int calculateRouteRewardPoints() {
        int routeCost = calculateRouteCostTotal();
        double routeRewardPoints = routeCost * ROUTE_TOTAL_COST_MULTIPLIER_TO_GET_REWARD_POINTS;

        return Math.toIntExact(Math.round(routeRewardPoints));
    }

    private int calculateRouteCostTotal() {
        int totalRouteCost = 0;

        for (PointOfInterest poi : pointsOfInterest) {
            if (poi.wasCreatedByUser()) {
                totalRouteCost +=
                        getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_points);
            } else {
                totalRouteCost +=
                        getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_points);
            }
        }

        return totalRouteCost;
    }

    // TODO: Refactor and generalize this into a User instance method.
    private void persistCurrentUserModifications() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentRouterReference();

        currentUserReference.child("hasFreeRouteCreation").setValue(currentRouter.getHasFreeRouteCreation());
        currentUserReference.child("points").setValue(currentRouter.getPoints());
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
}