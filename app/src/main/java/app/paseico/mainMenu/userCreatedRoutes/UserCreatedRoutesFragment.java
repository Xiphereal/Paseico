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

import java.util.ArrayList;
import java.util.List;

import app.paseico.R;
import app.paseico.data.Organization;

public class UserCreatedRoutesFragment extends Fragment {
    private ListView createdRoutesListView;
    private ArrayAdapter<String> createdRoutesListViewAdapter;
    private static List<String> createdRoutes = new ArrayList<>();

    //
    private List<Organization> organizations = new ArrayList<Organization>();
    private List<String> organizationsKeys = new ArrayList<String>();
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("organizations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    //Organization organization = snapshot.getValue(Organization.class);
                    //organizations.add(organization);
                    organizationsKeys.add(snapshot.getRef().getKey());
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
}
