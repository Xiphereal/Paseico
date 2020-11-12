package app.paseico.service;

import app.paseico.data.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

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
        firebaseFirestore.collection("route").add(route);
        return "Route " + route.getName() + " succesfully added to Firebase.";
    }
}
