package app.paseico;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        List<String> filteredRoutesNames = new ArrayList<>();
        List<Double> filteredRoutesEstimatedTime = new ArrayList<>();
        List<Double> filteredRoutesLength = new ArrayList<>();
        List<Integer> filteredRoutesRewardPoints = new ArrayList<>();

        for (Route route : filteredRoutes) {
            filteredRoutesNames.add(route.getName());
            filteredRoutesEstimatedTime.add(route.getEstimatedTime());
            filteredRoutesLength.add(route.getLength());
            filteredRoutesRewardPoints.add(route.getRewardPoints());
        }

        ArrayAdapter<String> adapter_filteredRoutes = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, filteredRoutesNames);

        ListView listView_filteredRoutes = (ListView) view.findViewById(R.id.listView_filteredRoutes);
        listView_filteredRoutes.setAdapter(adapter_filteredRoutes);


        view.findViewById(R.id.button_second).setOnClickListener(view1 -> NavHostFragment.findNavController(RouteListFragment.this)
                .navigate(R.id.action_RouteListFragment_to_SearchFragment));


    }
}