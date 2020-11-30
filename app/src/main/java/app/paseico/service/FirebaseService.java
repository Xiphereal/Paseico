package app.paseico.service;

import android.util.Log;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirebaseService {

    private static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static DatabaseReference getCurrentUserReference() {
        return firebaseDatabase.getReference("users").child(getCurrentUser().getUid());
    }

    public static void saveRoute(Route route) {
        firebaseFirestore.collection("route").add(route).addOnSuccessListener(documentReference -> {
            String createdRouteID = documentReference.getId();
            updateDatabaseRoute(createdRouteID, "id", createdRouteID);

            route.setId(createdRouteID);
        });

        System.out.println("Route " + route.getName() + " successfully added to Firebase.");
    }

    public static void updateDatabaseRoute(String routeId, String attribute, String newValue) {
        updateDatabaseRouteObject(routeId, attribute, newValue);
    }

    public static void updateDatabaseRoute(String routeId, String attribute, Double newValue) {
        updateDatabaseRouteObject(routeId, attribute, newValue);
    }

    public static void updateDatabaseRoute(String routeId, String attribute, int newValue) {
        updateDatabaseRouteObject(routeId, attribute, newValue);
    }

    public static void updateDatabaseRoute(String routeId, String attribute, List<PointOfInterest> newValue) {
        updateDatabaseRouteObject(routeId, attribute, newValue);
    }

    private static void updateDatabaseRouteObject(String routeId, String attribute, Object newValue) {
        DocumentReference reference = firebaseFirestore.collection("route").document(routeId);

        reference
                .update(attribute, newValue)
                .addOnSuccessListener(aVoid -> Log.d("Succes", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w("Failure", "Error updating document", e));

        System.out.println("Updated " + attribute + " as " + newValue.toString());
    }

    public static void deleteRoute(Route expectedRoute) {
        DocumentReference reference = firebaseFirestore.collection("route").document(expectedRoute.getId());

        reference.delete().addOnFailureListener(
                (exception) -> System.err.println("An error has occurred while trying to delete the route: " +
                        expectedRoute.getId() + System.lineSeparator() +
                        exception.getMessage()
                )
        );
    }
}
