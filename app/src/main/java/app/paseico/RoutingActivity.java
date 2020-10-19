package app.paseico;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class RoutingActivity extends FragmentActivity implements OnMapReadyCallback {
    //LocationManager class provides access to the system location services. These services allow applications
    // to obtain periodic updates of the device's geographical location, or to be notified when the
    // device enters the proximity of a given geographical location
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng usertolatlng;
    static Location lastKnownLocation = null;
    static Location currentDestination = null;
    private GoogleMap mMap;
    private boolean permissionGranted = false;

    public void centerMapOnLocation(Location location, String title) {
        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
        }
    }

    public void placePOIsFromRoute(ArrayList<String> POIsNames, ArrayList<LatLng> POIsLocations){

        for (int i = 1; i< POIsNames.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(POIsLocations.get(i)).title(POIsNames.get(i)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "Your Location");
            }
        }
    }

    protected void updateMyLatLng() {
        usertolatlng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //centerMapOnLocation(location, "Your Location");
                lastKnownLocation = location;
                if (currentDestination != null) {
                    if (lastKnownLocation.distanceTo(currentDestination) < 200) {
                        System.out.println("A MENOS DE 200 METROS");
                    } else {
                        System.out.println("A MUCHO MAS DE 200 METROS");
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            permissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                mMap.setMyLocationEnabled(true);
            }

        ArrayList<String> POIsNames = RouteStatusActivity.pointsOfInterests;
        ArrayList<LatLng> POIsLocations = RouteStatusActivity.locations;

        //It allows to represent the POIS from the route
        placePOIsFromRoute(POIsNames, POIsLocations);
        Intent intent = getIntent();
        //checking current user location
        if (intent.getIntExtra ("placeNumber", 0) == 0) {
            //zoom in on user location
            currentDestination = null;
            updateMyLatLng(); //Actualiza el latlng del usuario
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usertolatlng, 17));
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(RouteStatusActivity.locations.get(intent.getIntExtra("placeNumber",0)).latitude);
            placeLocation.setLongitude(RouteStatusActivity.locations.get(intent.getIntExtra("placeNumber",0)).longitude);
            centerMapOnLocation(placeLocation, RouteStatusActivity.pointsOfInterests.get(intent.getIntExtra("placeNumber",0)));
            currentDestination = placeLocation;
            updateMyLatLng();

        }


    }

    public void onBackPressed() {
        System.out.println("EL BACK HA SIDO PRESSEADO");
        currentDestination = null;
        finish();
    }

}