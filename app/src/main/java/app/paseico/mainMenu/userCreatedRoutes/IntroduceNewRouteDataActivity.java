package app.paseico.mainMenu.userCreatedRoutes;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.paseico.MainMenuActivity;
import app.paseico.R;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import app.paseico.data.Router;
import app.paseico.service.DistanceMatrixRequest;
import app.paseico.service.FirebaseService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IntroduceNewRouteDataActivity extends AppCompatActivity {

    private Router currentRouter;
    private Route newRoute;
    private int routeCost;

    private List<PointOfInterest> selectedPointsOfInterest = new ArrayList<>();

    final double ROUTE_TOTAL_COST_MULTIPLIER_TO_GET_REWARD_POINTS = 0.5;
    private boolean isOrganization;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_new_route_data);

        selectedPointsOfInterest = getIntent().getParcelableArrayListExtra("selectedPointsOfInterest");
        isOrganization = false;
        Bundle b = getIntent().getExtras();
        try{isOrganization = (boolean) b.get("organization");}catch(Exception e){isOrganization = false;}
        getCurrentUserFromDatabaseAsync();
    }

    /**
     * Gets the current User from the database asynchronously.
     */
    private void getCurrentUserFromDatabaseAsync() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentUserReference();
        //DatabaseReference currentOrganizationReference = FirebaseService.getCurrentOrganizationReference();

        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentRouter = snapshot.getValue(Router.class);

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
        ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.finalize_route_creation_button);

        extendedFloatingActionButton.setOnClickListener(view -> tryFinalizeRouteCreation());
    }

    /**
     * Checks for the User requirements for creating the new Route. If everything is fine, a confirmation dialog
     * appears and the Route creation finalizes. If anything goes wrong, a error dialogs appears and keeps the
     * previous state.
     */
    private void tryFinalizeRouteCreation() {
        if (currentRouter.getHasFreeRouteCreation()) {
            currentRouter.setHasFreeRouteCreation(false);
            showConfirmationDialog();
        } else {
            showRouteCreationSummaryDialog();
        }
    }

    private void showRouteCreationSummaryDialog() {
        routeCost = calculateRouteCost();

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

        for (PointOfInterest poi : selectedPointsOfInterest) {
            if (poi.wasCreatedByUser()) {
                totalRouteCost += getResources().getInteger(R.integer.user_newly_created_point_of_interest_cost);
            } else {
                totalRouteCost += getResources().getInteger(R.integer.google_maps_point_of_interest_cost);
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

        goToMainMenuActivity();
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

        newRoute = new Route(routeName,
                category,
                estimatedDistance,
                estimatedDuration,
                calculateRouteRewardPoints(),
                selectedPointsOfInterest,
                authorId);

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

    private int calculateRouteRewardPoints() {
        double routeRewardPoints = routeCost * ROUTE_TOTAL_COST_MULTIPLIER_TO_GET_REWARD_POINTS;

        return Math.toIntExact(Math.round(routeRewardPoints));
    }

    // TODO: Refactor and generalize this into a User instance method.
    private void persistCurrentUserModifications() {
        DatabaseReference currentUserReference = FirebaseService.getCurrentUserReference();

        currentUserReference.child("hasFreeRouteCreation").setValue(currentRouter.getHasFreeRouteCreation());
        currentUserReference.child("points").setValue(currentRouter.getPoints());
    }

    private void goToMainMenuActivity() {
        Intent goToMainMenuIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(goToMainMenuIntent);
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
}