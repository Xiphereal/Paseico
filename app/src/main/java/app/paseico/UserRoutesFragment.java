package app.paseico;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.paseico.data.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private List<String> routeNames = new ArrayList<>();

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
                            routeNames.add(routes.get(i).getName());
                        }
                       // MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(this, routeNames);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, routeNames);
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