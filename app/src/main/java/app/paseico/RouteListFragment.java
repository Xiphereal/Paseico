package app.paseico;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import app.paseico.data.Route;

public class RouteListFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Route[] filteredRoutes = RouteListFragmentArgs.fromBundle(getArguments()).getFilteredList();
        List<String> filteredRoutesList = new ArrayList<>();

        for (Route route : filteredRoutes) {
            filteredRoutesList.add(route.getName());
        }

        ArrayAdapter<String> adapter_filteredRoutes = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, filteredRoutesList);

        ListView listView_filteredRoutes = (ListView) view.findViewById(R.id.listView_filteredRoutes);
        listView_filteredRoutes.setAdapter(adapter_filteredRoutes);

        listView_filteredRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Route selectedRoute = filteredRoutes[position];

                Intent selectedRouteIntent = new Intent(getActivity(), RouteInformationActivity.class);
                selectedRouteIntent.putExtra("route", selectedRoute);
                startActivity(selectedRouteIntent);
            }
        });

        view.findViewById(R.id.btn_routeList_back).setOnClickListener(view1 -> NavHostFragment.findNavController(RouteListFragment.this)
                .navigate(R.id.action_RouteListFragment_to_SearchFragment));


    }
}