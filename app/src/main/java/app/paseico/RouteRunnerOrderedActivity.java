
package app.paseico;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import app.paseico.data.Route;

public class RouteRunnerOrderedActivity extends RouteRunnerBase {

    protected ArrayList<String> pointsOfInterestNames = (ArrayList<String>) super.pointsOfInterestNames;
    public ArrayList<LatLng> locations = (ArrayList<LatLng>) super.locations;
    public ArrayList<Boolean> isCompleted = (ArrayList<Boolean>) super.isCompleted;
    Route actualRoute;
    /*
        //google map object
        private GoogleMap mMap;
        //current and destination location objects
        Location myLocation = null;
        Location destinationLocation = null;
        protected LatLng start = null;
        protected LatLng end = null;
        //to get location permissions.
        private final static int LOCATION_REQUEST_CODE = 23;
        boolean locationPermission = false;
    
        //polyline object
        private List<Polyline> polylines = null;
    
        ArrayList<String> pointsOfInterestNames = new ArrayList<String>();
        ArrayList<LatLng> locations = new ArrayList<LatLng>();
        ArrayList<Boolean> isCompleted = new ArrayList<Boolean>();
        int actualPOI = 0;
        int poisLeft = 0;
        int rewpoints;
    
        Location currentDestination;
        TextView routeDisplay;
        String nombredeRuta = "Descubriendo Valencia";
    
        private app.paseico.data.Route actualRoute;
    
    */
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

        cancelRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                CreateConfirmation();
            }

        });

    }

    public void placePOIsFromRoute() {
        mMap.clear();

        for (int i = 0; i < pointsOfInterestNames.size(); i++) {
            if (isCompleted.get(i)) {
                mMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else {
                if (i == actualPOI) {
                    mMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                } else {
                    mMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)));
                }
            }
        }
        if(poisLeft == 0) {
            for(int j = 0; j < locations.size(); j++) {
                isCompleted.set(j, false);
                poisLeft++;
            }
            Intent intent = new Intent(RouteRunnerOrderedActivity.this, RouteFinishedActivity.class);
            intent.putExtra("route", actualRoute);
            System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ "+actualRoute.isOrdered());
            startActivity(intent);
            finish();
        }

    }


    void setNextOrderedPoint(int i){
        if (poisLeft > 0) {
            start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng destination = new LatLng(locations.get(i).latitude, locations.get(i).longitude);
            routeDisplay.setText("Próximo destino: \n" +pointsOfInterestNames.get(i));
            currentDestination = new Location(destination.toString());
            currentDestination.setLatitude(destination.latitude);
            currentDestination.setLongitude(destination.longitude);
            Findroutes(start, destination);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentDestination.getLatitude(), currentDestination.getLongitude()), 16f);
            mMap.animateCamera(cameraUpdate);
        }
        placePOIsFromRoute();
    }

    //to get user location
    public void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if (myLocation == null) {
                    myLocation = location;
                    LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            ltlng, 16f);

                    mMap.animateCamera(cameraUpdate);
                    actualPOI = 0;
                    setNextOrderedPoint(0);
                }

                if (currentDestination != null) {
                    myLocation = location;
                    if (myLocation.distanceTo(currentDestination) < 50) {
                        System.out.println("HAS COMPLETADO EL POI");
                        currentDestination = null;
                            isCompleted.set(actualPOI,true);
                            actualPOI++;
                            poisLeft--;
                            setNextOrderedPoint(actualPOI);
                    }
                }
            }
        });

        //get destination location when user click on map    ///DEACTIVATED THIS IS NOT NEEDED (WE CAN DELETE IT BUT LETS KEEP IT A BIT BECAUSE WE DON'T KNOW IF WE'RE GONNA NEED IT IN THE FUTURE)
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("¿Seguro que quieres salir?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
