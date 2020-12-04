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
import app.paseico.data.Router;
import app.paseico.data.User;
import app.paseico.service.FirebaseService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class ModifyRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TextInputLayout routeName;
    private ListView markedPOIsListView;

    private List<PointOfInterest> pointsOfInterest = new ArrayList<>();
    private List<PointOfInterest> originalPOIs = new ArrayList<>();

    private Marker userNewCustomPoiInCreation;
    private final List<String> createdMarkers = new ArrayList<>();
    private final List<String> markedPOIs = new ArrayList<>();

    private List<PointOfInterest> newPointsOfInterest = new ArrayList<>();

    private Router currentRouter;
    private Route retrievedRoute;
    private String retrievedRouteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_route);
        
        markedPOIsListView = findViewById(R.id.marked_pois_list_view);

        Intent retrievedIntent = this.getIntent();
        Bundle retrievedData = retrievedIntent.getExtras();

        retrievedRoute = (Route) retrievedData.get("route");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.new_route_map);
        mapFragment.getMapAsync(this);

        routeName = findViewById(R.id.route_name_textField);
        routeName.setHint(retrievedRoute.getName());
        //TODO: in order to make de name of the route modifiable, set enabled to true and build de logic
        routeName.setEnabled(false);

        pointsOfInterest = retrievedRoute.getPointsOfInterest();
        retrievedRouteId = getIntent().getStringExtra("routeID");

        getCurrentUserFromDatabaseAsync();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        for (int i = 0; i < pointsOfInterest.size(); i++) {
            Double latitude = pointsOfInterest.get(i).getLatitude();
            Double longitude = pointsOfInterest.get(i).getLongitude();
            String title = pointsOfInterest.get(i).getName();

            LatLng latLng = new LatLng(latitude, longitude);
            Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(title));

            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            createdMarkers.add(pointsOfInterest.get(i).getName());
            markedPOIs.add(pointsOfInterest.get(i).getName());
            originalPOIs.add(pointsOfInterest.get(i));
        }

        LatLng fakeUserPosition = new LatLng(39.475, -0.375);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(fakeUserPosition));

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        registerOnMapClick();
        registerOnMarkerClickListener();
        registerOnGoogleMapsPoiClickListener();

    }

    private void registerOnGoogleMapsPoiClickListener() {
        map.setOnPoiClickListener(poiSelected -> {
            tryDeleteUserNewCustomPoiInCreation();

            PointOfInterest poi = findClickedPointOfInterest(poiSelected.latLng, poiSelected.name);

            if (!isPointOfInterestSelected(poi) && !markerWasCreated(poiSelected.name)) {
                Marker markerOfThePoi = map
                        .addMarker(new MarkerOptions().position(poiSelected.latLng).title(poiSelected.name));

                selectPointOfInterest(markerOfThePoi, false);
                createdMarkers.add(poiSelected.name);
            }

            return;
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

        if(newPointsOfInterest.contains(poi)) {
            newPointsOfInterest.remove(poi);
        }

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
        map.setOnMapClickListener(tapPoint -> tryDeleteUserNewCustomPoiInCreation());
    }

    private void registerOnMarkerClickListener() {
        map.setOnMarkerClickListener(marker -> {
            tryDeleteUserNewCustomPoiInCreation();
            LatLng position = marker.getPosition();
            String title = marker.getTitle();

            PointOfInterest poi = findClickedPointOfInterest(position, title);

            if (isPointOfInterestSelected(poi)) {
                deselectPointOfInterest(marker, poi);
            } else {
                selectPointOfInterest(marker, false);
            }

            return true;
        });
    }

    private void getCurrentUserFromDatabaseAsync() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentUserReference();

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
        if (currentRouter.getHasFreeRouteCreation()) {
            currentRouter.setHasFreeRouteCreation(false);
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
            int currentUserPoints = currentRouter.getPoints();

            if (currentUserPoints >= routeCost) {
                currentRouter.setPoints(currentUserPoints - routeCost);
                showConfirmationDialog();
            } else {
                showNotEnoughPointsDialog();
            }
        });

        showDialog(builder);
    }

    private int calculateRouteCost() {
        int totalRouteCost = 0;

        for (PointOfInterest poi : newPointsOfInterest) {
            if(! originalPOIs.contains(poi)) {
                if (poi.wasCreatedByUser()) {
                    totalRouteCost += getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost);
                } else {
                    totalRouteCost += getResources().getInteger(R.integer.google_maps_point_of_interest_cost);
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
                .setTitle(R.string.route_creation_finalize_title)
                .setPositiveButton("OK", (dialog, which) -> {
                    // This remains empty because when the dialog is closed by tapping on 'OK' or outside it,
                    // it's considered to be dismissed in both cases, thus the call to the finalizer method must
                    // be done only on the dismiss listener.
                });

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


        FirebaseService.updateRoute(retrievedRouteId, "pointsOfInterest", pointsOfInterest );
    }

    // TODO: Refactor and generalize this into a User instance method.
    private void persistCurrentUserModifications() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentUserReference();

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
}