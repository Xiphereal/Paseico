package app.paseico.mainMenu.userCreatedRoutes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.R;
import app.paseico.data.PointOfInterest;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CreateNewRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected GoogleMap createNewRouteMap;
    protected final List<PointOfInterest> selectedPointsOfInterest = new ArrayList<>();

    protected ListView markedPOIsListView;
    protected final List<String> markedPOIs = new ArrayList<>();

    protected final List<String> createdMarkers = new ArrayList<>();
    protected final List<String> createdMarkersByUser = new ArrayList<>();

    protected Marker userNewCustomPoiInCreation;

    //Switch to show or not the poi list and buttons to order it
    protected Switch showPOIsSwitch;

    //buttons to change order
    protected Button poiUpButton;
    protected Button poiDownButton;

    protected TextView availablePoints;

    protected String selectedPOIinList = "";
    protected int positionOfPOIinList = 0;
    protected int nextPosition = 0;

    Location myLocation;

    private boolean isOrganization;
    private Router currentRouter;
    protected int accumulatedCost = 0;

    // This value has been copied from RouteRunnerBase. It should
    // represent the code for the location request.
    private static final int LOCATION_REQUEST_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_route);

        initializeMapFragment();

        checkIfUserIsAOrganization();

        getCurrentRouter();

        registerMarkedPOIsListView();
        registerUpAndDownButtons();
        registerOrderedRouteSwitch();

        registerGoToIntroduceNewRouteDataButtonTransition();
    }


    private void initializeMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.new_route_map);
        mapFragment.getMapAsync(this);
    }

    private void checkIfUserIsAOrganization() {
        Bundle bundle = getIntent().getExtras();

        isOrganization = false;

        try {
            isOrganization = (boolean) bundle.get("organization");
        } catch (Exception e) {
            isOrganization = false;
        }
    }

    private void setAvailablePoints() {
        availablePoints = findViewById(R.id.availablePoints);

        if (isOrganization) {
            TextView availablePointsTitle = findViewById(R.id.availablePointsTitle);
            availablePointsTitle.setText("");
            availablePoints.setText("");
        } else {
            availablePoints.setText(String.valueOf(currentRouter.getPoints()));
        }
    }

    private void getCurrentRouter() {
        DatabaseReference currentRouterReference = FirebaseService.getCurrentRouterReference();
        currentRouterReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentRouter = snapshot.getValue(Router.class);

                setAvailablePoints();
                setCosts();
                setAccumulatedCostTextView(accumulatedCost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The db connection failed: " + error.getMessage());
            }
        });
    }

    private void setAccumulatedCostTextView(int accumulatedCost) {
        TextView costs = findViewById(R.id.acumulatedCost);
        costs.setText(String.valueOf(accumulatedCost));

        if (!isOrganization && accumulatedCost > currentRouter.getPoints()) {
            costs.setTextColor(Color.parseColor("#F5340B"));
        } else {
            costs.setTextColor(Color.parseColor("#048C94"));
        }


    }

    private void setCosts() {
        TextView userPOIcost = findViewById(R.id.newPOIcost);
        String cost;

        if (isOrganization) {
            cost = String.valueOf(getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_euros));
            userPOIcost.setText(cost + "€");
        } else {
            cost = String.valueOf(getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_points));
            userPOIcost.setText(cost + " puntos");
        }
    }

    private void registerMarkedPOIsListView() {
        markedPOIsListView = findViewById(R.id.marked_pois_list_view);
        markedPOIsListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPOIinList = (String) markedPOIsListView.getItemAtPosition(position);
            positionOfPOIinList = position;
        });
    }

    private void registerUpAndDownButtons() {
        poiUpButton = findViewById(R.id.poiUp_button);
        poiUpButton.setOnClickListener(v -> moveUpSelectedPoiInList());

        poiDownButton = findViewById(R.id.poiDown_button);
        poiDownButton.setOnClickListener(v -> moveDownSelectedPoiInList());
    }

    private void moveUpSelectedPoiInList() {
        if (selectedPOIinList != "" && positionOfPOIinList != 0) {
            nextPosition = positionOfPOIinList - 1;
            moveSelectedPoiInList();
        } else {
            //Toast: select a poi of the list
            Toast.makeText(CreateNewRouteActivity.this, "Selecciona un POI debajo de otro.", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveDownSelectedPoiInList() {
        if (selectedPOIinList != "" && positionOfPOIinList != markedPOIs.size() - 1) {
            nextPosition = positionOfPOIinList + 1;
            moveSelectedPoiInList();
        } else {
            //Toast: select a poi of the list
            Toast.makeText(CreateNewRouteActivity.this, "Selecciona un POI encima de otro", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveSelectedPoiInList() {
        markedPOIs.set(positionOfPOIinList, markedPOIs.get(nextPosition));
        markedPOIs.set(nextPosition, selectedPOIinList);

        PointOfInterest poiSelectedInListView = selectedPointsOfInterest.get(positionOfPOIinList);
        selectedPointsOfInterest.set(positionOfPOIinList, selectedPointsOfInterest.get(nextPosition));
        selectedPointsOfInterest.set(nextPosition, poiSelectedInListView);

        updateMarkedPOIsListView();
        selectedPOIinList = "";
    }

    private void registerOrderedRouteSwitch() {
        showPOIsSwitch = (Switch) findViewById(R.id.showPOIs_switch);
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

    private void registerGoToIntroduceNewRouteDataButtonTransition() {
        ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.go_to_introduce_new_route_data_button);

        extendedFloatingActionButton.setOnClickListener(view -> goToIntroduceNewRouteDataActivity());
    }

    private void goToIntroduceNewRouteDataActivity() {
        if (selectedPointsOfInterest.size() > 1) {
            Intent goToIntroduceNewRouteDataIntent = new Intent(getApplicationContext(), IntroduceNewRouteDataActivity.class);

            goToIntroduceNewRouteDataIntent.putParcelableArrayListExtra("selectedPointsOfInterest",
                    (ArrayList<? extends Parcelable>) selectedPointsOfInterest);
            goToIntroduceNewRouteDataIntent.putExtra("organization", isOrganization);
            startActivity(goToIntroduceNewRouteDataIntent);
        } else {
            makeAlert("Por favor, seleccione dos o más puntos.");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        createNewRouteMap = googleMap;

        createNewRouteMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.create_route_style));

        LocationPermissionRequester.requestLocationPermission(this);

        tryMoveCameraToUserPosition();

        registerOnMapClick();
        registerOnMarkerClickListener();
        registerOnGoogleMapsPoiClickListener();
        registerOnMapLongClick();
    }

    /**
     * Try to move the camera to the user current position
     * if the coarse location permission are already granted.
     */
    private void tryMoveCameraToUserPosition() {
        if (LocationPermissionRequester.isCoarseLocationPermissionAlreadyGranted(this)) {
            moveCameraToUserPosition();
        }
    }

    private void moveCameraToUserPosition() {
        // Enables all the UI related to the user location and bearing.
        createNewRouteMap.setMyLocationEnabled(true);

        // The use of a deprecated method is due to convenience and ease of use.
        // Also, a similar approach has been taken in other parts of this codebase.
        createNewRouteMap.setOnMyLocationChangeListener(location -> {
            if (location == null) {
                return;
            }

            LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            createNewRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 15));

            // This is a hack to make the automatic centering just at startup and only once.
            createNewRouteMap.setOnMyLocationChangeListener(null);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (LocationPermissionRequester.didUserGrantCoarseLocationPermission(grantResults)) {
                moveCameraToUserPosition();
            }
        }
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

            // Check if the marker selected is associated with the POI in creation
            // If it not, we continue as planned.
            // If it is, we do nothing.
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

        updateAccumulatedCost(poi.wasCreatedByUser(), false);

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

        updateAccumulatedCost(createdByUser, true);

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
        });
    }

    private void updateAccumulatedCost(boolean createdByUser, boolean addPoint) {
        if (addPoint) {
            if (createdByUser) {
                accumulatedCost += isOrganization ?
                        getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_euros) :
                        getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_points);
            } else {
                accumulatedCost += isOrganization ?
                        getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_euros) :
                        getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_points);
            }
        } else {
            if (createdByUser) {
                accumulatedCost -= isOrganization ?
                        getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_euros) :
                        getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_points);
            } else {
                accumulatedCost -= isOrganization ?
                        getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_euros) :
                        getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_points);
            }
        }

        setAccumulatedCostTextView(accumulatedCost);
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

            if (compareWithPOIs(textInputEditText.getText().toString())) {

                userNewCustomPoiInCreation.setTitle(textInputEditText.getText().toString());
                textInputEditText.getText().clear();

                selectPointOfInterest(userNewCustomPoiInCreation, true);
                createdMarkers.add(userNewCustomPoiInCreation.getTitle());
                createdMarkersByUser.add(userNewCustomPoiInCreation.getTitle());

                userNewCustomPoiInCreation = null;

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private boolean compareWithPOIs(String routeName) {
        if (createdMarkersByUser.contains(routeName)) {
            makeAlert("Nombre ya existente. Escriba un nombre distinto.");
            return false;
        }

        if (routeName.trim().isEmpty()) {
            makeAlert("Por favor, escriba un nombre para el punto.");
            return false;
        }
        return true;
    }

    private void makeAlert(String s) {
        new AlertDialog.Builder(this).setTitle("Error al crear punto")
                .setMessage(s)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("MsgCancelled", "cancelado");
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("¿Seguro que quieres salir?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
