package app.paseico.service;

import app.paseico.data.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseService {

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static String saveRoute(Route route) {
        firebaseFirestore.collection("route").add(route);
        return "Route " + route.getName() + " succesfully added to Firebase.";
    }
}
