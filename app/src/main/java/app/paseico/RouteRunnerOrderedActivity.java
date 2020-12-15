
package app.paseico;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import app.paseico.data.Route;
import app.paseico.utils.LocationPermissionRequester;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class RouteRunnerOrderedActivity extends RouteRunnerBase {

    protected ArrayList<String> pointsOfInterestNames = (ArrayList<String>) super.pointsOfInterestNames;
    public ArrayList<LatLng> locations = (ArrayList<LatLng>) super.locations;
    public ArrayList<Boolean> isCompleted = (ArrayList<Boolean>) super.isCompleted;
    Route actualRoute;
    LatLng currentDest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_runner_ordered);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        //request location permission.
        requestPermision();
        InitiateAllVars();
        this.actualRoute = super.actualRoute;
        routeDisplay = findViewById(R.id.TextNextRoute);

        routeTitle.setText(nombredeRuta);

        cancelRoute.setOnClickListener(view -> CreateConfirmation());
    }

    public void placePOIsFromRoute() {
        routeRunnerMap.clear();

        for (int i = 0; i < pointsOfInterestNames.size(); i++) {
            if (isCompleted.get(i)) {
                routeRunnerMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else {
                if (i == actualPOI) {
                    routeRunnerMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                } else {
                    routeRunnerMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)));
                }
            }
        }

        if (poisLeft == 0) {
            for (int j = 0; j < locations.size(); j++) {
                isCompleted.set(j, false);
                poisLeft++;
            }
            Intent intent = new Intent(RouteRunnerOrderedActivity.this, RouteFinishedActivity.class);
            intent.putExtra("route", actualRoute);
            startActivity(intent);
            finish();
        }
    }

    void setNextOrderedPoint(int i) {
        routeRunnerMap.clear();

        if (poisLeft > 0) {
            start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng destination = new LatLng(locations.get(i).latitude, locations.get(i).longitude);
            currentDest = destination;
            routeDisplay.setText("Próximo destino: \n" + pointsOfInterestNames.get(i));
            currentDestination = new Location(destination.toString());
            currentDestination.setLatitude(destination.latitude);
            currentDestination.setLongitude(destination.longitude);
            Findroutes(start, currentDest);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentDestination.getLatitude(), currentDestination.getLongitude()), 16f);
            routeRunnerMap.animateCamera(cameraUpdate);
        }

        placePOIsFromRoute();
    }

    //to get user location
    public void getMyLocation() {
        if (!LocationPermissionRequester.isCoarseLocationPermissionAlreadyGranted(this)) {
            return;
        }

        routeRunnerMap.setMyLocationEnabled(true);
        routeRunnerMap.setOnMyLocationChangeListener(location -> {
            if (myLocation == null) {
                myLocation = location;

                actualPOI = 0;
                setNextOrderedPoint(0);
            }

            if (currentDestination != null) {
                myLocation = location;

                if (myLocation.distanceTo(currentDestination) < 50) {
                    System.out.println("HAS COMPLETADO EL POI");
                    currentDestination = null;
                    isCompleted.set(actualPOI, true);
                    actualPOI++;
                    poisLeft--;
                    setNextOrderedPoint(actualPOI);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("¿Seguro que quieres salir?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            //if user pressed "yes", then he is allowed to exit from application
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            //if user select "No", just cancel this dialog and continue with app
            dialog.cancel();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
