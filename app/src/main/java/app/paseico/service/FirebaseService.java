package app.paseico.service;

import app.paseico.data.Route;
import com.google.firebase.firestore.FirebaseFirestore;


public class FirebaseService{

    public static String saveRoute(Route route) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("route").add(route);
        return "Route " + route.getName() + " succesfully added to Firebase.";
    }

}
