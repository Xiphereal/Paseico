package app.paseico;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import app.paseico.adapters.FilteredListAdapter;
import app.paseico.data.Route;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class UserRoutesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference
    private DatabaseReference myActualUserRef;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser fireBaseUser = firebaseAuth.getCurrentUser();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference routesReference = database.collection("route");
    private List<Route> routes = new ArrayList<>();

    private List<String> filteredRoutesNames = new ArrayList<>();
    private List<String> filteredRoutesEstimatedHours = new ArrayList<>();
    private List<String> filteredRoutesEstimatedMinutes = new ArrayList<>();
    private List<String> filteredRoutesKm = new ArrayList<>();
    private List<String> filteredRoutesMeters = new ArrayList<>();
    private List<String> filteredRoutesRewardPoints = new ArrayList<>();
    private List<String> filteredRoutesAreOrdered = new ArrayList<>();
    private List<Integer> filteredRoutesIcons = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserRoutesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserRoutesFragment newInstance(int columnCount) {
        UserRoutesFragment fragment = new UserRoutesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_routes_list, container, false);
        ListView userRoutesList = view.findViewById(R.id.UserRoutesList);

        myActualUserRef = myUsersRef.child(fireBaseUser.getUid());

        routesReference.whereEqualTo("authorId", fireBaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            routes.add(document.toObject(Route.class));
                        }
                        for (int i = 0; i < routes.size(); i++ ) {
                            filteredRoutesNames.add(routes.get(i).getName());
                            int hours = (int) (routes.get(i).getEstimatedTime()/60);
                            int minutes = (int) (routes.get(i).getEstimatedTime() - (hours*60));
                            filteredRoutesEstimatedHours.add(String.valueOf(hours));
                            filteredRoutesEstimatedMinutes.add(String.valueOf(minutes));

                            int km = (int) (routes.get(i).getLength()/1000);
                            int meters = (int) (routes.get(i).getLength()) - km*1000;
                            filteredRoutesKm.add(String.valueOf(km));
                            filteredRoutesMeters.add(String.valueOf(meters));

                            filteredRoutesRewardPoints.add(String.valueOf(routes.get(i).getRewardPoints()));
                            filteredRoutesAreOrdered.add(String.valueOf(routes.get(i).isOrdered()));

                            //Obtain route theme
                            String RouteCategory = routes.get(i).getTheme();
                            System.out.println("categoria " + RouteCategory);
                            int index = CategoryManager.ConvertCategoryToIntDrawable(RouteCategory);
                            System.out.println("indice " + index);
                            filteredRoutesIcons.add(index);
                        }
                       // MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(this, routeNames);
                        //ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, routeNames);

                        FilteredListAdapter adapter = new FilteredListAdapter(this.getContext(), filteredRoutesNames, filteredRoutesEstimatedHours, filteredRoutesEstimatedMinutes,filteredRoutesKm, filteredRoutesMeters,
                                filteredRoutesRewardPoints, filteredRoutesIcons, filteredRoutesAreOrdered);
                        userRoutesList.setAdapter(adapter);
                    }
                });

        userRoutesList.setOnItemClickListener((parent, view1, position, id) -> {
            Route selectedRoute = routes.get(position);

            String routeID = selectedRoute.getId();

            Intent selectedRouteIntent = new Intent(getActivity(), RouteInfModifyActivity.class);
            selectedRouteIntent.putExtra("route", selectedRoute);
            selectedRouteIntent.putExtra("routeID", routeID);
            startActivity(selectedRouteIntent);
        });
        return view;
    }



}