package app.paseico.mainMenu.userCreatedRoutes;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.koalap.geofirestore.GeoQuery;
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

import app.paseico.CategoryManager;
import app.paseico.R;
import app.paseico.RouteInformationActivity;
import app.paseico.data.Route;

public class UserCreatedRoutesFragment extends Fragment {
    private ListView organizationRoutesListView;
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


    FilteredListAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user_created_routes, container, false);

        organizationRoutesListView = root.findViewById(R.id.organization_routes_list_view);

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
    private void readOrganizations(){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference referenceRoutes = database.collection("route");
        DatabaseReference referenceOrganizations = FirebaseDatabase.getInstance().getReference("organizations");

        referenceOrganizations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
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

                                        adapter = new FilteredListAdapter(getContext(), orgRoutesNames, orgRoutesEstimatedTime, orgRoutesLength,
                                                orgRoutesRewardPoints, orgRoutesIcons, orgRoutesAreOrdered, orgNames);

                                        organizationRoutesListView.setAdapter(adapter);

                                        organizationRoutesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Route selectedRoute = organizationRoutes.get(position);

                                                Intent selectedRouteIntent = new Intent(getActivity(), RouteInformationActivity.class);
                                                selectedRouteIntent.putExtra("route", selectedRoute);
                                                startActivity(selectedRouteIntent);
                                            }
                                        });
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
            super(context, R.layout.item_route_search, R.id.routeName, names);

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
            TextView organizationName = row.findViewById(R.id.organizationNameText);


            ImageView ListViewImage = (ImageView) row.findViewById(R.id.imageViewIcon);

            myNames.setText(names.get(position));
            myEstimatedTimes.setText(estimatedTimes.get(position));
            myLengths.setText(lengths.get(position));
            myPoints.setText(points.get(position));
            organizationName.setText(organization.get(position));

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
