package app.paseico.mainMenu.userCreatedRoutes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import app.paseico.R;
import app.paseico.data.Organization;
import app.paseico.data.Route;

public class UserCreatedRoutesFragment extends Fragment {
    private ListView createdRoutesListView;
    private ArrayAdapter<String> createdRoutesListViewAdapter;
    private static List<String> createdRoutes = new ArrayList<>();

    //
    private List<Organization> organizations = new ArrayList<Organization>();
    private List<String> organizationsKeys = new ArrayList<String>();
    private List<Route> organizationRoutes = new ArrayList<Route>();
    private String organizationKey = "";

    private List<String> orgRoutesNames = new ArrayList<>();
    private List<String> orgRoutesEstimatedTime = new ArrayList<>();
    private List<String> orgRoutesLength = new ArrayList<>();
    private List<String> orgRoutesRewardPoints = new ArrayList<>();
    private List<String> orgRoutesAreOrdered = new ArrayList<>();
    private List<String> orgRoutesCategory = new ArrayList<>();
    private List<Integer> orgRoutesIcons = new ArrayList<>();
    //

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user_created_routes, container, false);

        createdRoutesListView = root.findViewById(R.id.created_routes_list_view);

        //TODO: the created routes list should display only routes created by the user,
        // currently it shown routes created independent of current user.
        //updateCreatedRoutesListView();
        readOrganizations();

        registerCreateNewRouteButtonTransition(root);

        return root;
    }

    private void updateCreatedRoutesListView() {
        createdRoutesListViewAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, createdRoutes);
        createdRoutesListView.setAdapter(createdRoutesListViewAdapter);
    }

    private void registerCreateNewRouteButtonTransition(View root) {
        ExtendedFloatingActionButton extendedFloatingActionButton = root.findViewById(R.id.create_new_route_button);
        extendedFloatingActionButton.setOnClickListener(view -> {
            Intent createNewRouteIntent = new Intent(getActivity(), CreateNewRouteActivity.class);
            startActivity(createNewRouteIntent);
        });
    }
    //
    private void readOrganizations(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference referenceRoutes = database.collection("route");

        DatabaseReference referenceOrganizations = FirebaseDatabase.getInstance().getReference("organizations");
        referenceOrganizations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    //Organization organization = snapshot.getValue(Organization.class);
                    //organizations.add(organization);
                    organizationKey = snapshot.getRef().getKey();
                    organizationsKeys.add(organizationKey);
                    System.out.println("la clave de la organizacion es" +  snapshot.getRef().getKey());

                    referenceRoutes.whereEqualTo("authorId", organizationKey)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot snapshotRuta : task.getResult()) {

                                        Route route = snapshotRuta.toObject(Route.class);

                                        orgRoutesNames.add(route.getName());
                                        orgRoutesEstimatedTime.add(Double.toString(route.getEstimatedTime()));
                                        orgRoutesLength.add(Double.toString(route.getLength()));
                                        orgRoutesRewardPoints.add(Integer.toString(route.getRewardPoints()));
                                        orgRoutesAreOrdered.add(Integer.toString(route.isOrdered()));
                                        orgRoutesCategory.add(route.getTheme());


                                        System.out.println("la ruta de nombre" + route.getName() + "id " + route.getId()+ " estimated time" + route.getEstimatedTime());
                                        System.out.println("logitud " + route.getLength() + " reward point " + route.getRewardPoints());
                                        System.out.println("ordenación " + route.isOrdered() + " categoria  " + route.getTheme());

                                    }
                                    System.out.println("bieeeeennnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
                                } else {
                                    System.out.println("eeeeeeeeeeeeeeeeeeeeeeeerrrrrrrrrrrrrrro");
                                }
                            });
                }
                //once I get organizations keys I retrieve their routes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static List<String> getCreatedRoutes() {
        return createdRoutes;
    }
}





                /*referenceRoutes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshotRoute : snapshot.getChildren()){
                           Route organizationRoute = snapshotRoute.getValue(Route.class);
                           System.out.println("el nombre de la ruta creada por la organzizacion es" + organizationRoute.getName());
                           organizationRoutes.add(organizationRoute);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
