package app.paseico;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import app.paseico.data.PointOfInterest;
import app.paseico.utils.LocationPermissionRequester;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class RouteRunnerNotOrderedActivity extends RouteRunnerBase {

    protected ArrayList<String> pointsOfInterestNames = new ArrayList<String>();
    public ArrayList<LatLng> locations = new ArrayList<LatLng>();
    public ArrayList<Boolean> isCompleted = new ArrayList<Boolean>();

    // This is involved in a hacky solution for making the automatic camera
    // centering in the User current location just once.
    private boolean firstLocationUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actualPOI = -1;
        arrayAdapter = new ArrayAdapterRoutes(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_runner);

        // Request location permission.
        requestPermision();
        InitiateAllVars();

        // Init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TextView routeTitle = findViewById(R.id.textViewTitleRoutingActivity);

        ImageButton cancelRoute = findViewById(R.id.buttonCancelRoute);
        if (b != null && actualRoute == null) {
            actualRoute = (app.paseico.data.Route) b.get("route");
            nombredeRuta = actualRoute.getName();
            rewpoints = actualRoute.getRewardPoints();
        }

        List<PointOfInterest> routePois = actualRoute.getPointsOfInterest();
        for (PointOfInterest pois : routePois) {
            pointsOfInterestNames.add(pois.getName());
            locations.add(new LatLng(pois.getLatitude(), pois.getLongitude()));
        }

        for (int i = 0; i < locations.size(); i++) {
            isCompleted.add(false);
        }

        routeTitle.setText(nombredeRuta);

        listView = findViewById(R.id.TextNextRoute);
        listView.setAdapter(arrayAdapter);
        listView.setBackgroundResource(R.drawable.layout_bgt);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!isCompleted.get(i)) {
                LatLng destination = new LatLng(locations.get(i).latitude, locations.get(i).longitude);

                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                //start route finding
                routeRunnerMap.clear();
                placePOIsFromRoute();
                Findroutes(start, destination);
                currentDestination = new Location(destination.toString());
                currentDestination.setLatitude(destination.latitude);
                currentDestination.setLongitude(destination.longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentDestination.getLatitude(), currentDestination.getLongitude()), 16f);
                routeRunnerMap.animateCamera(cameraUpdate);
                actualPOI = i;
                arrayAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "POI ya visitada!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelRoute.setOnClickListener(view -> CreateConfirmation());
    }

    public void placePOIsFromRoute() {
        routeRunnerMap.clear();

        for (int i = 0; i < pointsOfInterestNames.size(); i++) {
            if (isCompleted.get(i)) {
                routeRunnerMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else {
                routeRunnerMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)));
            }
        }

        if (poisLeft == 0) {
            for (int j = 0; j < locations.size(); j++) {
                isCompleted.set(j, false);
                poisLeft++;
            }
            Intent intent = new Intent(RouteRunnerNotOrderedActivity.this, RouteFinishedActivity.class);
            intent.putExtra("route", actualRoute);
            startActivity(intent);
            finish();
        }
    }

    public void getMyLocation() {
        if (!LocationPermissionRequester.isCoarseLocationPermissionAlreadyGranted(this)) {
            return;
        }

        routeRunnerMap.setMyLocationEnabled(true);

        routeRunnerMap.setOnMyLocationChangeListener(location -> {
            if (myLocation == null) {
                myLocation = location;
            }

            // This is involved in a hacky solution for making the automatic camera
            // centering in the User current location just once.
            if (firstLocationUpdate) {
                LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                routeRunnerMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 16));

                firstLocationUpdate = false;
            }

            if (currentDestination != null) {
                myLocation = location;
                if (myLocation.distanceTo(currentDestination) < 50) {
                    System.out.println("HAS COMPLETADO EL POI");
                    currentDestination = null;
                    if (actualPOI > -1) {
                        isCompleted.set(actualPOI, true);
                        arrayAdapter.notifyDataSetChanged();
                        actualPOI = -1;
                    }
                    poisLeft--;
                    placePOIsFromRoute();
                }
            }
        });
    }

    public class ArrayAdapterRoutes extends BaseAdapter {
        Context context;

        public ArrayAdapterRoutes(Context c) {
            context = c;
        }

        @Override
        public int getCount() {
            return pointsOfInterestNames.size();
        }

        @Override
        public Object getItem(int i) {
            return pointsOfInterestNames.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowview = View.inflate(context, R.layout.activity_runner_textview, null);
            TextView nom = rowview.findViewById(R.id.textPOI);

            if (isCompleted.get(i)) {
                nom.setBackgroundColor(Color.GREEN);
            }
            if (actualPOI == i) {
                nom.setText(Html.fromHtml("<b>" + pointsOfInterestNames.get(i) + "</b>"));
            } else {
                nom.setText(pointsOfInterestNames.get(i));
            }

            return rowview;
        }
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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
