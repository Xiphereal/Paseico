package app.paseico.service;

import android.util.Log;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.koalap.geofirestore.GeoFire;
import com.koalap.geofirestore.GeoLocation;

import java.util.List;

public class FirebaseService {

    private static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static DatabaseReference getCurrentRouterReference() {
        return firebaseDatabase.getReference("users").child(getCurrentUser().getUid());
    }

    public static DatabaseReference getCurrentOrganizationReference() {
        return firebaseDatabase.getReference("organizations").child(getCurrentUser().getUid());
    }

    public static void saveRoute(Route route) {
        firebaseFirestore.collection("route").add(route).addOnSuccessListener(documentReference -> {
            String createdRouteID = documentReference.getId();
            updateRoute(createdRouteID, "id", createdRouteID);
            setGeoFireRoute(createdRouteID, route);
        });

        System.out.println("Route " + route.getName() + " successfully added to Firebase.");
    }

    public static void updateRoute(String routeId, String attribute, String newValue) {
        updateRouteObject(routeId, attribute, newValue);
    }

    public static void updateRoute(String routeId, String attribute, Double newValue) {
        updateRouteObject(routeId, attribute, newValue);
    }

    public static void updateRoute(String routeId, String attribute, int newValue) {
        updateRouteObject(routeId, attribute, newValue);
    }

    public static void updateRoute(String routeId, String attribute, List<PointOfInterest> newValue) {
        updateRouteObject(routeId, attribute, newValue);
    }

    private static void updateRouteObject(String routeId, String attribute, Object newValue) {
        DocumentReference reference = firebaseFirestore.collection("route").document(routeId);

        reference
                .update(attribute, newValue)
                .addOnSuccessListener(aVoid -> Log.d("Succes", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w("Failure", "Error updating document", e));

        System.out.println("Updated " + attribute + " as " + newValue.toString());
    }

    private static void setGeoFireRoute(String id, Route route){
        CollectionReference ref = FirebaseFirestore.getInstance().collection("geofire");
        List<PointOfInterest> pois = route.getPointsOfInterest();
        PointOfInterest first = pois.get(0);
        GeoLocation geoLocation = new GeoLocation(first.getLatitude(), first.getLongitude());
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(id, geoLocation,
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, Exception exception) {
                        if (exception != null) {
                            System.err.println("There was an error saving the location to GeoFire: " + exception.toString());
                        } else {
                            System.out.println("Location saved on server successfully!");
                        }
                    }
                });
    }
}
