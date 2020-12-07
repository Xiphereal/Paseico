package app.paseico.mainMenu.userCreatedRoutes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.koalap.geofirestore.GeoFire;
import com.koalap.geofirestore.GeoLocation;
import com.koalap.geofirestore.GeoQuery;
import com.koalap.geofirestore.GeoQueryDataValueEventListener;
import com.koalap.geofirestore.GeoQueryDocumentChange;
import com.koalap.geofirestore.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.List;

import app.paseico.CategoryManager;
import app.paseico.R;
import app.paseico.RouteInformationActivity;
import app.paseico.data.Route;
import app.paseico.mainMenu.searcher.MyRecyclerViewAdapter;
import app.paseico.mainMenu.searcher.RouteSearchFragment;
import app.paseico.mainMenu.searcher.RouteSearchFragmentDirections;

public class UserCreatedRoutesFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {
    private ListView nearRoutesListView;
    private ArrayAdapter<String> createdRoutesListViewAdapter;
    private static List<String> createdRoutes = new ArrayList<>();

    private List<String> organizationsKeys = new ArrayList<String>();
    private List<Route> organizationRoutes = new ArrayList<Route>();
    private String organizationKey = "";

    private List<String> orgRoutesNames = new ArrayList<>();
    private List<String> orgRoutesEstimatedTime = new ArrayList<>();
    private List<String> orgRoutesLength = new ArrayList<>();
    private List<String> orgRoutesRewardPoints = new ArrayList<>();
    private List<String> orgRoutesAreOrdered = new ArrayList<>();
    private List<String> orgNames = new ArrayList<>();
    private List<Integer> orgRoutesIcons = new ArrayList<>();



    private List<String> nearKeys = new ArrayList<String>();
    private List<Route> nearRoutes = new ArrayList<Route>();

    private List<String> nearRoutesNames = new ArrayList<>();
    private List<String> nearRoutesEstimatedTime = new ArrayList<>();
    private List<String> nearRoutesLength = new ArrayList<>();
    private List<String> nearRoutesRewardPoints = new ArrayList<>();
    private List<String> nearRoutesAreOrdered = new ArrayList<>();
    private List<String> nearNames = new ArrayList<>();
    private List<Integer> nearRoutesIcons = new ArrayList<>();

    protected LocationManager locationManager;
    protected LocationListener locationListener;

    private Location myLocation = null;

    FilteredListAdapter adapter;
    MyRecyclerViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;

    CollectionReference ref = FirebaseFirestore.getInstance().collection("geofire");
    GeoFire geoFire = new GeoFire(ref);
    private FusedLocationProviderClient fusedLocationClient;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user_created_routes, container, false);

        nearRoutesListView = root.findViewById(R.id.near_routes_list_view);
        recyclerView = root.findViewById(R.id.organization_routes_recyclerview);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastUserLocation();
        readOrganizations();


        registerCreateNewRouteButtonTransition(root);

        return root;
    }

    private void registerCreateNewRouteButtonTransition(View root) {
        ExtendedFloatingActionButton extendedFloatingActionButton = root.findViewById(R.id.create_new_route_button);
        extendedFloatingActionButton.setOnClickListener(view -> {
            Intent createNewRouteIntent = new Intent(getActivity(), CreateNewRouteActivity.class);
            startActivity(createNewRouteIntent);
        });
    }

    //
    private void readOrganizations() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference referenceRoutes = database.collection("route");
        DatabaseReference referenceOrganizations = FirebaseDatabase.getInstance().getReference("organizations");

        referenceOrganizations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    organizationKey = snapshot.getRef().getKey();
                    organizationsKeys.add(organizationKey);

                    String organizationName = snapshot.child("name").getValue(String.class);

                    referenceRoutes.whereEqualTo("authorId", organizationKey)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot snapshotRuta : task.getResult()) {

                                        Route route = snapshotRuta.toObject(Route.class);

                                        organizationRoutes.add(route);
                                        orgRoutesNames.add(route.getName());
                                        orgRoutesEstimatedTime.add(Double.toString(route.getEstimatedTime()));
                                        orgRoutesLength.add(Double.toString(route.getLength()));
                                        orgRoutesRewardPoints.add(Integer.toString(route.getRewardPoints()));
                                        orgRoutesAreOrdered.add(Integer.toString(route.isOrdered()));
                                        orgNames.add(organizationName);

                                        String routeCategory = route.getTheme();
                                        int index = CategoryManager.ConvertCategoryToIntDrawable(routeCategory);
                                        orgRoutesIcons.add(index);



                                        LinearLayoutManager horizontalLayoutManager
                                                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                        recyclerView.setLayoutManager(horizontalLayoutManager);
                                        recyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), orgRoutesNames, orgRoutesEstimatedTime, orgRoutesLength,
                                                orgRoutesRewardPoints, orgRoutesIcons, orgRoutesAreOrdered, orgNames);
                                        recyclerViewAdapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Route selectedRoute = organizationRoutes.get(position);

                                                Intent selectedRouteIntent = new Intent(getActivity(), RouteInformationActivity.class);
                                                selectedRouteIntent.putExtra("route", selectedRoute);
                                                startActivity(selectedRouteIntent);
                                            }
                                        });

                                        recyclerView.setAdapter(recyclerViewAdapter);

                                    }
                                } else {
                                    Log.d("Ruta2", "task is " + task.getException());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static List<String> getCreatedRoutes() {
        return createdRoutes;
    }

    private void loadNearRoutes() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference routesReference = database.collection("route");

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(myLocation.getLatitude(), myLocation.getLongitude()), 5);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                routesReference.whereEqualTo("id", key).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshotRuta : task.getResult()) {

                            Route route = snapshotRuta.toObject(Route.class);

                            nearRoutes.add(route);
                            nearRoutesNames.add(route.getName());
                            nearRoutesEstimatedTime.add(Double.toString(route.getEstimatedTime()));
                            nearRoutesLength.add(Double.toString(route.getLength()));
                            nearRoutesRewardPoints.add(Integer.toString(route.getRewardPoints()));
                            nearRoutesAreOrdered.add(Integer.toString(route.isOrdered()));


                            String routeCategory = route.getTheme();
                            int index = CategoryManager.ConvertCategoryToIntDrawable(routeCategory);
                            nearRoutesIcons.add(index);

                            if(getActivity()!=null) {
                                adapter = new FilteredListAdapter(getActivity(), nearRoutesNames, nearRoutesEstimatedTime, nearRoutesLength,
                                        nearRoutesRewardPoints, nearRoutesIcons, nearRoutesAreOrdered, nearNames);

                                nearRoutesListView.setAdapter(adapter);

                                nearRoutesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Route selectedRoute = nearRoutes.get(position);

                                        Intent selectedRouteIntent = new Intent(getActivity(), RouteInformationActivity.class);
                                        selectedRouteIntent.putExtra("route", selectedRoute);
                                        startActivity(selectedRouteIntent);
                                    }
                                });
                            }



                        }
                    } else {
                        Log.d("Ruta error", "Error getting documents: ", task.getException());
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(Exception error) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "You clicked " + recyclerViewAdapter.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
    }


    public void getLastUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            myLocation = location;
                            loadNearRoutes();
                        }
                    }
                });
    }

    class FilteredListAdapter extends ArrayAdapter<String> {

        Context context;
        List<String> names;
        List<String> estimatedTimes;
        List<String> lengths;
        List<String> points;
        List<Integer> icons;
        List<String> areOrdered;
        List<String> organization;


        FilteredListAdapter(Context context, List<String> names, List<String> estimatedTimes, List<String> lengths, List<String> points,
                            List<Integer> icons, List<String> areOrdered, List<String> organization) {
            super(context, R.layout.item_route_search
                    , R.id.routeName, names);

            this.context = context;
            this.names = names;
            this.estimatedTimes = estimatedTimes;
            this.lengths = lengths;
            this.points = points;
            this.icons = icons;
            this.areOrdered = areOrdered;
            this.organization = organization;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_route_search, parent, false);

            TextView myNames = row.findViewById(R.id.routeName);
            TextView myEstimatedTimes = row.findViewById(R.id.routeDuration);
            TextView myLengths = row.findViewById(R.id.routeLenght);
            TextView myPoints = row.findViewById(R.id.routeReward);
            TextView orderedRoute = row.findViewById(R.id.textView_orderedRouteResult);
            //TextView organizationName = row.findViewById(R.id.organizationNameText);


            ImageView ListViewImage = (ImageView) row.findViewById(R.id.imageViewIcon);

            myNames.setText(names.get(position));
            myEstimatedTimes.setText(estimatedTimes.get(position));
            myLengths.setText(lengths.get(position));
            myPoints.setText(points.get(position));
//            organizationName.setText(organization.get(position));

            String isOrdered = areOrdered.get(position);
            if (isOrdered.equals("0")){
                isOrdered = "No";
            } else {
                isOrdered = "Sí";
            }

            orderedRoute.setText(isOrdered);

            ListViewImage.setImageResource(icons.get(position));

            return row;
        }

    }

}
