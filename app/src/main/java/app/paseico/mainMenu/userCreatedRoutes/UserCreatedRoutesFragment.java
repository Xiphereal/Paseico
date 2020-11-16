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
import androidx.lifecycle.ViewModelProvider;
import app.paseico.CreateNewRouteActivity;
import app.paseico.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UserCreatedRoutesFragment extends Fragment {
    private ListView createdRoutesListView;
    private ArrayAdapter<String> createdRoutesListViewAdapter;
    private static List<String> createdRoutes = new ArrayList<>();
    private RoutesViewModel routesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        routesViewModel = new ViewModelProvider(this).get(RoutesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_user_created_routes, container, false);

        createdRoutesListView = root.findViewById(R.id.created_routes_list_view);
        //TODO: the created routes list should display only routes created by the user,
        // currently it shown routes created independent of current user.
        updateCreatedRoutesListView();

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

    public static List<String> getCreatedRoutes() {
        return createdRoutes;
    }
}