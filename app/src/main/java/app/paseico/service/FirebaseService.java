package app.paseico.service;

import android.util.Log;
import androidx.annotation.NonNull;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirebaseService {

    private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static DatabaseReference getCurrentUserReference() {
        return firebaseDatabase.getReference("users").child(getCurrentUser().getUid());
    }

    public static String saveRoute(Route route) {
        firebaseFirestore.collection("route").add(route).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String createdRouteID = documentReference.getId();
                updateRoute(createdRouteID, "id", createdRouteID);
            }
        });

        return "Route " + route.getName() + " succesfully added to Firebase.";
    }

    public static String updateRoute(String routeId, String attribute, String newValue) {
        return FirebaseService.updateRouteObject(routeId, attribute,newValue);
    }
    public static String updateRoute(String routeId, String attribute, Double newValue) {
        return FirebaseService.updateRouteObject(routeId, attribute,newValue);
    }
    public static String updateRoute(String routeId, String attribute, int newValue) {
        return FirebaseService.updateRouteObject(routeId, attribute,newValue);
    }
    public static String updateRoute(String routeId, String attribute, List<PointOfInterest> newValue) {
        return FirebaseService.updateRouteObject(routeId, attribute,newValue);
    }


    private static String updateRouteObject(String routeId, String attribute, Object newValue) {
        DocumentReference reference = firebaseFirestore.collection("route").document(routeId);

        reference
                .update(attribute, newValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Succes", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failure", "Error updating document", e);
                    }
                });
        return "Updated " + attribute + " as " + newValue.toString() ;
    }
}
