package app.paseico.mainMenu.userCreatedRoutes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.paseico.CategoryManager;
import app.paseico.R;
import app.paseico.RouteInformationActivity;
import app.paseico.adapters.FilteredListAdapter;
import app.paseico.adapters.MyRecyclerViewAdapter;
import app.paseico.data.Route;
import app.paseico.utils.LocationPermissionRequester;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.*;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.koalap.geofirestore.GeoFire;
import com.koalap.geofirestore.GeoLocation;
import com.koalap.geofirestore.GeoQuery;
import com.koalap.geofirestore.GeoQueryEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserCreatedRoutesFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {
    private ListView nearRoutesListView;
    private static List<String> createdRoutes = new ArrayList<>();

    private List<String> organizationsKeys = new ArrayList<String>();
    private List<Route> organizationRoutes = new ArrayList<Route>();
    private String organizationKey = "";

    private List<String> orgRoutesNames = new ArrayList<>();
    private List<String> orgRoutesEstimatedHours = new ArrayList<>();
    private List<String> orgRoutesEstimatedMinutes = new ArrayList<>();
    private List<String> orgRoutesKm = new ArrayList<>();
    private List<String> orgRoutesMeters = new ArrayList<>();
    private List<String> orgRoutesRewardPoints = new ArrayList<>();
    private List<String> orgRoutesAreOrdered = new ArrayList<>();
    private List<String> orgNames = new ArrayList<>();
    private List<Integer> orgRoutesIcons = new ArrayList<>();

    private List<String> nearKeys = new ArrayList<String>();
    private List<Route> nearRoutes = new ArrayList<Route>();

    private List<String> nearRoutesNames = new ArrayList<>();
    private List<String> nearRoutesEstimatedHours = new ArrayList<>();
    private List<String> nearRoutesEstimatedMinutes = new ArrayList<>();
    private List<String> nearRoutesKm = new ArrayList<>();
    private List<String> nearRoutesMeters = new ArrayList<>();
    private List<String> nearRoutesRewardPoints = new ArrayList<>();
    private List<String> nearRoutesAreOrdered = new ArrayList<>();
    private List<String> nearNames = new ArrayList<>();
    private List<Integer> nearRoutesIcons = new ArrayList<>();

    private Location myLocation = null;

    FilteredListAdapter adapter;
    MyRecyclerViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;

    CollectionReference ref = FirebaseFirestore.getInstance().collection("geofire");
    GeoFire geoFire = new GeoFire(ref);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_created_routes, container, false);

        nearRoutesListView = root.findViewById(R.id.near_routes_list_view);
        recyclerView = root.findViewById(R.id.organization_routes_recyclerview);

        LocationPermissionRequester.requestLocationPermission(this.getActivity());

        tryLoadUserNearRoutes();

        readOrganizations();

        registerCreateNewRouteButtonTransition(root);

        return root;
    }

    private void registerCreateNewRouteButtonTransition(View root) {
        ExtendedFloatingActionButton extendedFloatingActionButton = root.findViewById(R.id.create_new_route_button);
        extendedFloatingActionButton.setOnClickListener(view -> {
            checkLocation();
        });
    }

    public void checkLocation() {
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            Intent createNewRouteIntent = new Intent(getActivity(), CreateNewRouteActivity.class);
            startActivity(createNewRouteIntent);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Para poder usar Paseico necesitas activar la ubicación")
                .setCancelable(false)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No activar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

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

                                        int hours = (int) (route.getEstimatedTime() / 60);
                                        int minutes = (int) (route.getEstimatedTime() - (hours * 60));
                                        orgRoutesEstimatedHours.add(String.valueOf(hours));
                                        orgRoutesEstimatedMinutes.add(String.valueOf(minutes));

                                        int km = (int) (route.getLength() / 1000);
                                        int meters = (int) (route.getLength()) - km * 1000;
                                        orgRoutesKm.add(String.valueOf(km));
                                        orgRoutesMeters.add(String.valueOf(meters));

                                        orgRoutesRewardPoints.add(Integer.toString(route.getRewardPoints()));
                                        orgRoutesAreOrdered.add(Integer.toString(route.isOrdered()));
                                        orgNames.add(organizationName);

                                        String routeCategory = route.getTheme();
                                        int index = CategoryManager.ConvertCategoryToIntDrawable(routeCategory);
                                        orgRoutesIcons.add(index);


                                        LinearLayoutManager horizontalLayoutManager
                                                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                        recyclerView.setLayoutManager(horizontalLayoutManager);
                                        recyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), orgRoutesNames, orgRoutesEstimatedHours, orgRoutesEstimatedMinutes, orgRoutesKm, orgRoutesMeters,
                                                orgRoutesRewardPoints, orgRoutesIcons, orgRoutesAreOrdered, orgNames);

                                        recyclerViewAdapter.setClickListener((view, position) -> {
                                            Route selectedRoute = organizationRoutes.get(position);

                                            Intent selectedRouteIntent = new Intent(getActivity(), RouteInformationActivity.class);
                                            selectedRouteIntent.putExtra("route", selectedRoute);
                                            startActivity(selectedRouteIntent);
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

                            int hours = (int) (route.getEstimatedTime() / 60);
                            int minutes = (int) (route.getEstimatedTime() - (hours * 60));
                            nearRoutesEstimatedHours.add(String.valueOf(hours));
                            nearRoutesEstimatedMinutes.add(String.valueOf(minutes));

                            int km = (int) (route.getLength() / 1000);
                            int meters = (int) (route.getLength()) - km * 1000;
                            nearRoutesKm.add(String.valueOf(km));
                            nearRoutesMeters.add(String.valueOf(meters));

                            nearRoutesRewardPoints.add(Integer.toString(route.getRewardPoints()));
                            nearRoutesAreOrdered.add(Integer.toString(route.isOrdered()));

                            String routeCategory = route.getTheme();
                            int index = CategoryManager.ConvertCategoryToIntDrawable(routeCategory);
                            nearRoutesIcons.add(index);

                            if (getActivity() != null) {
                                adapter = new FilteredListAdapter(getActivity(), nearRoutesNames, nearRoutesEstimatedHours, nearRoutesEstimatedMinutes, nearRoutesKm, nearRoutesMeters,
                                        nearRoutesRewardPoints, nearRoutesIcons, nearRoutesAreOrdered);
                                //nearNames
                                nearRoutesListView.setAdapter(adapter);

                                nearRoutesListView.setOnItemClickListener((parent, view, position, id) -> {
                                    Route selectedRoute = nearRoutes.get(position);

                                    Intent selectedRouteIntent = new Intent(getActivity(), RouteInformationActivity.class);
                                    selectedRouteIntent.putExtra("route", selectedRoute);
                                    startActivity(selectedRouteIntent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == LocationPermissionRequester.LOCATION_REQUEST_CODE) {
            if (LocationPermissionRequester.didUserGrantCoarseLocationPermission(grantResults)) {
                tryLoadUserNearRoutes();
            }
        }
    }

    private void tryLoadUserNearRoutes() {
        if (!LocationPermissionRequester.isCoarseLocationPermissionAlreadyGranted(this.getContext())) {
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {

                        myLocation = location;
                        loadNearRoutes();
                    }
                });
    }
}
