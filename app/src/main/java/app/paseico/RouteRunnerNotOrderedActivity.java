
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

public class RouteRunnerNotOrderedActivity extends RouteRunnerBase  {

    protected ArrayList<String> pointsOfInterestNames = new ArrayList<String>();
    public ArrayList<LatLng> locations = new ArrayList<LatLng>();
    public ArrayList<Boolean> isCompleted = new ArrayList<Boolean>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actualPOI = -1;
        arrayAdapter = new ArrayAdapterRutas(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_runner);
        //request location permission.
        requestPermision();
        InitiateAllVars();

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TextView routeTitle = findViewById(R.id.textViewTitleRoutingActivity);


        ImageButton cancelRoute = findViewById(R.id.buttonCancelRoute);
        if (b != null && actualRoute == null) {actualRoute = (app.paseico.data.Route) b.get("route");
        nombredeRuta = actualRoute.getName();
        rewpoints = actualRoute.getRewardPoints();}

        List<PointOfInterest> routePois = actualRoute.getPointsOfInterest();
        for (int i = 0; i < routePois.size(); i++) {
            pointsOfInterestNames.add(routePois.get(i).getName());
            locations.add(new LatLng(routePois.get(i).getLatitude(), routePois.get(i).getLongitude()));
        }

        for (int i = 0; i < locations.size(); i++) {
            isCompleted.add(false);
            poisLeft++;
        }


        /*else { if (pointsOfInterestNames.isEmpty()){


            //nombredeRuta = "Descubriendo Valencia";

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

            actualRoute = new app.paseico.data.Route(nombredeRuta, "Monumentos", 10, 10, 100, pois);
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
        }*/
        routeTitle.setText(nombredeRuta);

        listView = findViewById(R.id.TextNextRoute);
        listView.setAdapter(arrayAdapter);
        cancelRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

            }

        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isCompleted.get(i)) {
                    LatLng destination = new LatLng(locations.get(i).latitude, locations.get(i).longitude);

                    start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    //start route finding
                    mMap.clear();
                    placePOIsFromRoute();
                    Findroutes(start, destination);
                    currentDestination = new Location(destination.toString());
                    currentDestination.setLatitude(destination.latitude);
                    currentDestination.setLongitude(destination.longitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(currentDestination.getLatitude(), currentDestination.getLongitude()), 16f);
                    mMap.animateCamera(cameraUpdate);
                    actualPOI = i;
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(),"POI ya visitada!",Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                mMap.addMarker(new MarkerOptions().position(locations.get(i)).title(pointsOfInterestNames.get(i)));
            }
        }
        if(poisLeft == 0) {
            for(int j = 0; j < locations.size(); j++) {
                isCompleted.set(j, false);
                poisLeft++;
            }
            Intent intent = new Intent(RouteRunnerNotOrderedActivity.this, RouteFinishedActivity.class);
            intent.putExtra("route", actualRoute);
            startActivity(intent);
            finish();
        }

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
                }

                if (currentDestination != null) {
                    myLocation = location;
                    if (myLocation.distanceTo(currentDestination) < 50) {
                        System.out.println("HAS COMPLETADO EL POI");
                        currentDestination = null;
                        if(actualPOI > -1){
                        isCompleted.set(actualPOI,true);
                        arrayAdapter.notifyDataSetChanged();
                            actualPOI = -1;}
                        poisLeft--;
                        placePOIsFromRoute();
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
    

    public class ArrayAdapterRutas extends BaseAdapter {

        Context context;


        public ArrayAdapterRutas(Context c){
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
            View rowview = View.inflate(context, R.layout.activity_runner_textview,null);
                TextView nom = rowview.findViewById(R.id.textPOI);


                        if (isCompleted.get(i)) {
                            nom.setBackgroundColor(Color.GREEN);
                        }
                        if (actualPOI == i){
                            nom.setText(Html.fromHtml("<b>"+pointsOfInterestNames.get(i)+"</b>"));
                        } else {
                            nom.setText(pointsOfInterestNames.get(i));
                        }

                return rowview;
        }
    }


}
