package app.paseico;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import app.paseico.data.PointOfInterestPaseico;

public class MainActivity<Polyline> extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

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

    static ArrayList<String> pointsOfInterestNames = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayList<Boolean> isCompleted = new ArrayList<Boolean>();
    static int actualPOI;
    static int poisLeft = 0;
    static ArrayAdapter arrayAdapter;

    static Location currentDestination;
    static ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request location permission.
        requestPermision();

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listView = findViewById(R.id.ListViewRoute);
        if (pointsOfInterestNames.isEmpty() && locations.isEmpty()) {
            PointOfInterestPaseico POI1 = new PointOfInterestPaseico("Mercado central", 39.4736, -0.3790);
            PointOfInterestPaseico POI2 = new PointOfInterestPaseico("Torres de Quart", 39.4758, -0.3839);
            PointOfInterestPaseico POI3 = new PointOfInterestPaseico("Torres de Serranos", 39.479284, -0.376167);
            PointOfInterestPaseico POI4 = new PointOfInterestPaseico("El Miguelete", 39.475326 , -0.375607);
            PointOfInterestPaseico POI5 = new PointOfInterestPaseico("Lonja de la Seda", 39.47441 , -0.378259);
            PointOfInterestPaseico POI6 = new PointOfInterestPaseico("Plaza de la virgen", 39.476391 , -0.375277);

            pointsOfInterestNames.add(POI1.getName());
            pointsOfInterestNames.add(POI2.getName());
            pointsOfInterestNames.add(POI3.getName());
            pointsOfInterestNames.add(POI4.getName());
            pointsOfInterestNames.add(POI5.getName());
            pointsOfInterestNames.add(POI6.getName());
            locations.add(new LatLng(POI1.getLatitude(), POI1.getLongitude()));
            locations.add(new LatLng(POI2.getLatitude(), POI2.getLongitude()));
            locations.add(new LatLng(POI3.getLatitude(), POI3.getLongitude()));
            locations.add(new LatLng(POI4.getLatitude(), POI4.getLongitude()));
            locations.add(new LatLng(POI5.getLatitude(), POI5.getLongitude()));
            locations.add(new LatLng(POI6.getLatitude(), POI6.getLongitude()));

            List<PointOfInterestPaseico> pois = new ArrayList<PointOfInterestPaseico>();
            pois.add(POI1);
            pois.add(POI2);
            pois.add(POI3);
            pois.add(POI4);
            pois.add(POI5);
            pois.add(POI6);

            app.paseico.data.Route actualRoute = new app.paseico.data.Route("Descubriendo valencia", pois);
            TextView routeTitle = findViewById(R.id.textViewTitleRoutingActivity);
            routeTitle.setText(actualRoute.getName());

            for(int i = 0; i < locations.size(); i++) {
                isCompleted.add(false);
                poisLeft++;
            }
        }



        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pointsOfInterestNames);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if (isCompleted.get(i) == false) {


                LatLng destination = new LatLng(locations.get(i).latitude,locations.get(i).longitude);

                start=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                //start route finding
                mMap.clear();
                placePOIsFromRoute(pointsOfInterestNames, locations, isCompleted);
                Findroutes(start,destination);
                currentDestination = new Location(destination.toString());
                currentDestination.setLatitude(destination.latitude);
                currentDestination.setLongitude(destination.longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentDestination.getLatitude(),currentDestination.getLongitude()), 16f);
                mMap.animateCamera(cameraUpdate);
                actualPOI = i;
            }
        });

    }

    public void placePOIsFromRoute(ArrayList<String> POIsNames, ArrayList<LatLng> POIsLocations, ArrayList<Boolean> POIsCompleted){
        mMap.clear();

            for (int i = 0; i < POIsNames.size(); i++) {
                if (POIsCompleted.get(i)) {
                    mMap.addMarker(new MarkerOptions().position(POIsLocations.get(i)).title(POIsNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                } else {
                    mMap.addMarker(new MarkerOptions().position(POIsLocations.get(i)).title(POIsNames.get(i)));
                }
            }
        if(poisLeft == 0) {
            Intent intent = new Intent(MainActivity.this, RouteFinishedActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
                    getMyLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    //to get user location
    private void getMyLocation() {
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

                myLocation=location;
                LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                       ltlng, 16f);

                mMap.animateCamera(cameraUpdate);

                if (currentDestination != null) {
                    myLocation = location;
                    if (myLocation.distanceTo(currentDestination) < 50) {
                        System.out.println("HAS COMPLETADO EL POI");
                        currentDestination = null;
                        isCompleted.set(actualPOI,true);
                         // MARK IT IN GREEN COLOR
                        poisLeft--;
                        placePOIsFromRoute(pointsOfInterestNames, locations, isCompleted);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();
        placePOIsFromRoute(pointsOfInterestNames, locations, isCompleted);

    }


    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if(Start==null || End==null) {
            Toast.makeText(MainActivity.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyAMLXmyf_ai85V6PdlA7jYhjL-nSgHl7mA")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//    Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {

    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = (Polyline) mMap.addPolyline(polyOptions);
                polylineStartLatLng= ((com.google.android.gms.maps.model.Polyline) polyline).getPoints().get(0);
                int k= ((com.google.android.gms.maps.model.Polyline) polyline).getPoints().size();
                polylineEndLatLng= ((com.google.android.gms.maps.model.Polyline) polyline).getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }
        }
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start,end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end);
    }
}