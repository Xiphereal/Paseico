
package app.paseico;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import app.paseico.data.PointOfInterest;

public class RouteRunnerOrderedActivity<Polyline> extends FragmentActivity implements OnMapReadyCallback,
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_runner_ordered);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        //request location permission.
        requestPermision();

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TextView routeTitle = findViewById(R.id.textViewTitleRoutingActivity);


        ImageButton cancelRoute = findViewById(R.id.buttonCancelRoute);
        routeDisplay = findViewById(R.id.TextNextRoute);
        if (b != null && actualRoute == null) {
            actualRoute = (app.paseico.data.Route) b.get("route");
            nombredeRuta = actualRoute.getName();
            rewpoints = actualRoute.getRewardPoints();

            List<PointOfInterest> routePois = actualRoute.getPointsOfInterest();
            for (int i = 0; i < routePois.size(); i++) {
                pointsOfInterestNames.add(routePois.get(i).getName());
                locations.add(new LatLng(routePois.get(i).getLatitude(), routePois.get(i).getLongitude()));
            }

            for (int i = 0; i < locations.size(); i++) {
                isCompleted.add(false);
                poisLeft++;
            }

        }
        else {


            nombredeRuta = "Descubriendo Valencia";

            PointOfInterest POI1 = new PointOfInterest(39.4736, -0.3790, "Mercado central");
            PointOfInterest POI2 = new PointOfInterest(39.4758, -0.3839, "Torre de Quart");
            PointOfInterest POI3 = new PointOfInterest(39.479284, -0.376167, "Torres de Serranos");
            PointOfInterest POI4 = new PointOfInterest(39.475326, -0.375607, "El Miguelete");
            PointOfInterest POI5 = new PointOfInterest(39.47441, -0.378259, "Lonja de la Seda");
            PointOfInterest POI6 = new PointOfInterest(39.476391, -0.375277, "Plaza de la Virgen");

            List<PointOfInterest> pois = new ArrayList<PointOfInterest>();
            pois.add(POI1);
            pois.add(POI2);
            pois.add(POI3);
            pois.add(POI4);
            pois.add(POI5);
            pois.add(POI6);

            actualRoute = new app.paseico.data.Route(nombredeRuta, "Monumentos", 10, 10, 100, pois, true);
            List<PointOfInterest> routePois = actualRoute.getPointsOfInterest();
            rewpoints = 100;
            for (int i = 0; i < routePois.size(); i++) {
                pointsOfInterestNames.add(routePois.get(i).getName());
                locations.add(new LatLng(routePois.get(i).getLatitude(), routePois.get(i).getLongitude()));
            }

            for (int i = 0; i < locations.size(); i++) {
                isCompleted.add(false);
                poisLeft++;
            }
        }

        routeTitle.setText(nombredeRuta);

        cancelRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                CreateConfirmation();
            }

        });

    }

    public void CreateConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de que deseas cancelar?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                poisLeft = 0;
                for(int j = 0; j < locations.size(); j++) {
                    isCompleted.set(j, false);
                    poisLeft++;
                }
                Intent intent = new Intent(RouteRunnerOrderedActivity.this, RouteInformationActivity.class);
                intent.putExtra("route", actualRoute);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    public void placePOIsFromRoute(ArrayList<String> POIsNames, ArrayList<LatLng> POIsLocations, ArrayList<Boolean> POIsCompleted) {
        mMap.clear();

        for (int i = 0; i < POIsNames.size(); i++) {
            if (POIsCompleted.get(i)) {
                mMap.addMarker(new MarkerOptions().position(POIsLocations.get(i)).title(POIsNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else {
                if (i == actualPOI) {
                    mMap.addMarker(new MarkerOptions().position(POIsLocations.get(i)).title(POIsNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                } else {
                    mMap.addMarker(new MarkerOptions().position(POIsLocations.get(i)).title(POIsNames.get(i)));
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

    void setNextOrderedPoint(int i){
        if (poisLeft > 0) {
            start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng destination = new LatLng(locations.get(i).latitude, locations.get(i).longitude);
            routeDisplay.setText(pointsOfInterestNames.get(i));
            currentDestination = new Location(destination.toString());
            currentDestination.setLatitude(destination.latitude);
            currentDestination.setLongitude(destination.longitude);
            Findroutes(start, destination);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentDestination.getLatitude(), currentDestination.getLongitude()), 16f);
            mMap.animateCamera(cameraUpdate);
        }
        placePOIsFromRoute(pointsOfInterestNames, locations, isCompleted);
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

                if (myLocation == null) {
                    myLocation = location;
                    LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            ltlng, 16f);

                    mMap.animateCamera(cameraUpdate);
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
                        System.out.println("WWWWWWWWWWWWWWWWWW "+ poisLeft);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();
        placePOIsFromRoute(pointsOfInterestNames, locations, isCompleted);
    }




    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(RouteRunnerOrderedActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

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
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
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
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = (Polyline) mMap.addPolyline(polyOptions);
                polylineStartLatLng = ((com.google.android.gms.maps.model.Polyline) polyline).getPoints().get(0);
                int k = ((com.google.android.gms.maps.model.Polyline) polyline).getPoints().size();
                polylineEndLatLng = ((com.google.android.gms.maps.model.Polyline) polyline).getPoints().get(k - 1);
                polylines.add(polyline);


            } else {

            }
        }
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start, end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start, end);
    }


}
