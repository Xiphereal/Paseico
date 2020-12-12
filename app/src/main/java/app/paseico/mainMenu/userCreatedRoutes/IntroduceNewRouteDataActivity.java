package app.paseico.mainMenu.userCreatedRoutes;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import app.paseico.MainMenuActivity;
import app.paseico.MainMenuOrganizationActivity;
import app.paseico.R;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.Router;
import app.paseico.mainMenu.searcher.RouteSearchFragment;
import app.paseico.mainMenu.searcher.RouteSearchFragmentDirections;
import app.paseico.service.DistanceMatrixRequest;
import app.paseico.service.FirebaseService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.koalap.geofirestore.GeoFire;
import com.koalap.geofirestore.GeoLocation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IntroduceNewRouteDataActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton extendedFloatingActionButton;

    private Router currentRouter;
    private int routeCost;
    private int isOrdered = 0;

    private List<PointOfInterest> selectedPointsOfInterest = new ArrayList<>();

    final double ROUTE_TOTAL_COST_MULTIPLIER_TO_GET_REWARD_POINTS = 0.5;

    private boolean isOrganization;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_introduce_new_route_data);

        registerOrderedRouteSwitch();

        selectedPointsOfInterest = getIntent().getParcelableArrayListExtra("selectedPointsOfInterest");

        checkIfUserIsAOrganization();

        getCurrentUserFromDatabaseAsync();


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

    /**
     * Gets the current User from the database asynchronously.
     */
    private void getCurrentUserFromDatabaseAsync() {
        DatabaseReference currentUserReference;

        currentUserReference = isOrganization ?
                FirebaseService.getCurrentOrganizationReference() :
                FirebaseService.getCurrentRouterReference();

        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isOrganization) {
                    currentRouter = snapshot.getValue(Router.class);
                }

                // Registering this callback here ensures that the button
                // action is only performed when the User is ready.
                registerTryFinalizeNewRouteCreationListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The db connection failed: " + error.getMessage());
            }
        });
    }

    private void registerTryFinalizeNewRouteCreationListener() {
        extendedFloatingActionButton = findViewById(R.id.finalize_route_creation_button);

        extendedFloatingActionButton.setOnClickListener(view -> tryFinalizeRouteCreation());
    }

    /**
     * Checks for the User requirements for creating the new Route. If everything is fine, a confirmation dialog
     * appears and the Route creation finalizes. If anything goes wrong, a error dialogs appears and keeps the
     * previous state.
     */
    private void tryFinalizeRouteCreation() {
        TextInputEditText textInputEditText = findViewById(R.id.route_name_textInputEditText);
        if(textInputEditText.getText().toString().isEmpty()) {
            String dialogMessage = getResources().getString(R.string.route_creation_empty_name);
            AlertDialog.Builder builder = setUpBuilder(dialogMessage);
            showDialog(builder);
            return;
        }
        checkExistingRouteName();
    }

    private void showRouteCreationByOrganizationSummaryDialog() {
        routeCost = calculateRouteCost();

        String dialogMessage = "El coste de la ruta en creación es de: " + routeCost + " €";
        AlertDialog.Builder builder = setUpBuilder(dialogMessage);

        builder.setOnDismissListener(dialog -> {
            String dialogNewMessage = "El importe ha sido tramitado con éxito. Recibirá un correo con la factura correspondiente";
            AlertDialog.Builder newBuilder = setUpBuilder(dialogNewMessage);
            newBuilder.setOnDismissListener(newDialog -> showConfirmationDialog());
            showDialog(newBuilder);
        });

        showDialog(builder);
    }

    private void showRouteCreationSummaryDialog() {
        routeCost = calculateRouteCost();

        String dialogMessage = getResources().getString(R.string.route_creation_summary_message, routeCost);
        AlertDialog.Builder builder = setUpBuilder(dialogMessage);

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

        for (PointOfInterest poi : selectedPointsOfInterest) {
            if (poi.wasCreatedByUser()) {
                totalRouteCost += isOrganization ?
                        getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_euros) :
                        getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost_in_points);
            } else {
                totalRouteCost += isOrganization ?
                        getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_euros) :
                        getResources().getInteger(R.integer.google_maps_point_of_interest_cost_in_points);
            }
        }

        return totalRouteCost;
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
        if(isOrganization){
            goToMainMenuOrganizationActivity();
        } else {
            persistCurrentUserModifications();
            goToMainMenuActivity();
        }
    }

    private void createNewRoute() {
        TextInputEditText textInputEditText = findViewById(R.id.route_name_textInputEditText);
        Spinner categorySpinner = findViewById(R.id.introduce_new_route_data_route_category_spinner);

        String routeName = textInputEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String authorId = FirebaseService.getCurrentUser().getUid();


        // Get the estimated duration and distance for the newly created Route via a API request.
        DistanceMatrixRequest distanceMatrixRequest = calculateRouteMetrics();

        double estimatedDistance = distanceMatrixRequest != null ?
                distanceMatrixRequest.getRouteEstimatedDistance() :
                0;

        double estimatedDuration = distanceMatrixRequest != null ?
                distanceMatrixRequest.getRouteEstimatedDuration() :
                0;

        Route newRoute = new Route(routeName,
                category,
                estimatedDistance,
                estimatedDuration,
                calculateRouteRewardPoints(),
                selectedPointsOfInterest,
                authorId,
                isOrdered);

        FirebaseService.saveRoute(newRoute);

        //We add the created route name to the createdRoutes before returning to the main activity.
        UserCreatedRoutesFragment.getCreatedRoutes().add(newRoute.getName());
    }

    /**
     * Make a request to the Distance Matrix API to get the estimated duration and distance of the {@link Route}.
     *
     * @return The {@link DistanceMatrixRequest} containing the response. Can be queried with
     * getRouteEstimatedDistance() and getRouteEstimatedDuration().
     */
    private DistanceMatrixRequest calculateRouteMetrics() {

        if (selectedPointsOfInterest.size() < 2) {
            return null;
        }

        DistanceMatrixRequest distanceMatrixRequest = new DistanceMatrixRequest(getApplicationContext());
        distanceMatrixRequest
                .addOptionalParameter("mode=walking")
                .addPointsOfInterest(selectedPointsOfInterest)
                .send();

        return distanceMatrixRequest;
    }

    private void registerOrderedRouteSwitch() {
        Switch orderedRouteSwitch = (Switch) findViewById(R.id.ordered_route_switch);
        orderedRouteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isOrdered = 1;
                } else {
                    isOrdered = 0;
                }
            }
        });
    }

    private int calculateRouteRewardPoints() {
        double routeRewardPoints = routeCost * ROUTE_TOTAL_COST_MULTIPLIER_TO_GET_REWARD_POINTS;

        return Math.toIntExact(Math.round(routeRewardPoints));
    }

    // TODO: Refactor and generalize this into a User instance method.
    private void persistCurrentUserModifications() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentRouterReference();

        currentUserReference.child("hasFreeRouteCreation").setValue(currentRouter.getHasFreeRouteCreation());
        currentUserReference.child("points").setValue(currentRouter.getPoints());
    }

    private void goToMainMenuActivity() {
        Intent goToMainMenuIntent = new Intent(getApplicationContext(), MainMenuActivity.class);

        // Finishes all the opened activities.
        goToMainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(goToMainMenuIntent);
        finish();
    }

    private void goToMainMenuOrganizationActivity() {
        Intent goToMainMenuOrganizationIntent = new Intent(getApplicationContext(), MainMenuOrganizationActivity.class);
        goToMainMenuOrganizationIntent.putExtra("organization", isOrganization);

        // Finishes all the opened activities.
        goToMainMenuOrganizationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(goToMainMenuOrganizationIntent);
        finish();
    }

    private void showNotEnoughPointsDialog() {
        String dialogMessage = getResources().getString(R.string.route_creation_not_enough_points_message);
        AlertDialog.Builder builder = setUpBuilder(dialogMessage);

        builder.setOnDismissListener(dialog -> {
            // This remains empty because we want the app to do nothing in this case.
        });

        showDialog(builder);
    }

    private void showDialog(AlertDialog.Builder builder) {
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkExistingRouteName(){

        TextInputEditText textInputEditText = findViewById(R.id.route_name_textInputEditText);
        String routeName = textInputEditText.getText().toString();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference routesReference = database.collection("route");

        routesReference.whereEqualTo("name",routeName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(task.getResult().size()<=0){
                    if (isOrganization) {
                        showRouteCreationByOrganizationSummaryDialog();
                    } else {
                        if (currentRouter.getHasFreeRouteCreation()) {
                            currentRouter.setHasFreeRouteCreation(false);
                            showConfirmationDialog();
                        } else {
                            showRouteCreationSummaryDialog();
                        }
                    }
                }else{
                    String dialogMessage = "Ya existe una ruta con ese nombre, escoja otro";
                    AlertDialog.Builder builder = setUpBuilder(dialogMessage);
                    showDialog(builder);


                }
            }
        });

    }
}
