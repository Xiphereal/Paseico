package app.paseico.mainMenu.searcher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import app.paseico.CategoryManager;
import app.paseico.FilteredListAdapter;
import app.paseico.R;
import app.paseico.RouteInformationActivity;

import app.paseico.data.Route;
import java.util.ArrayList;
import java.util.List;
public class RouteSearchResultFragment extends Fragment {



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_search_result, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Route[] filteredRoutes = RouteSearchResultFragmentArgs.fromBundle(getArguments()).getFilteredList();
        List<String> filteredRoutesNames = new ArrayList<>();
        List<String> filteredRoutesEstimatedHours = new ArrayList<>();
        List<String> filteredRoutesEstimatedMinutes = new ArrayList<>();
        List<String> filteredRoutesKm = new ArrayList<>();
        List<String> filteredRoutesMeters = new ArrayList<>();
        List<String> filteredRoutesRewardPoints = new ArrayList<>();
        List<String> filteredRoutesAreOrdered = new ArrayList<>();

        List<Integer> filteredRoutesIcons = new ArrayList<>();


        for (Route route : filteredRoutes) {
            filteredRoutesNames.add(route.getName());

            int hours = (int) (route.getEstimatedTime()/60);
            int minutes = (int) (route.getEstimatedTime() - (hours*60));
            filteredRoutesEstimatedHours.add(String.valueOf(hours));
            filteredRoutesEstimatedMinutes.add(String.valueOf(minutes));

            int km = (int) (route.getLength()/1000);
            int meters = (int) (route.getLength()) - km*1000;
            filteredRoutesKm.add(String.valueOf(km));
            filteredRoutesMeters.add(String.valueOf(meters));

            filteredRoutesRewardPoints.add(String.valueOf(route.getRewardPoints()));
            filteredRoutesAreOrdered.add(String.valueOf(route.isOrdered()));

            //Obtain route theme
            String RouteCategory = route.getTheme();
            System.out.println("categoria " + RouteCategory);
            int index = CategoryManager.ConvertCategoryToIntDrawable(RouteCategory);
            System.out.println("indice " + index);
            filteredRoutesIcons.add(index);

        }

        ListView listView_filteredRoutes = (ListView) view.findViewById(R.id.listView_filteredRoutes);

        FilteredListAdapter adapter = new FilteredListAdapter(this.getContext(), filteredRoutesNames, filteredRoutesEstimatedHours, filteredRoutesEstimatedMinutes,filteredRoutesKm, filteredRoutesMeters,
                filteredRoutesRewardPoints, filteredRoutesIcons, filteredRoutesAreOrdered);
        listView_filteredRoutes.setAdapter(adapter);

        // Open RouteInfo screen.
        listView_filteredRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Route selectedRoute = filteredRoutes[position];

                Intent selectedRouteIntent = new Intent(getActivity(), RouteInformationActivity.class);
                selectedRouteIntent.putExtra("route", selectedRoute);
                startActivity(selectedRouteIntent);
            }
        });

    }
}
