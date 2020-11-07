package app.paseico;

import android.content.Context;
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

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        List<String> filteredRoutesEstimatedTime = new ArrayList<>();
        List<String> filteredRoutesLength = new ArrayList<>();
        List<String> filteredRoutesRewardPoints = new ArrayList<>();

        for (Route route : filteredRoutes) {
            filteredRoutesNames.add(route.getName());
            filteredRoutesEstimatedTime.add(String.valueOf(route.getEstimatedTime()));
            filteredRoutesLength.add(String.valueOf(route.getLength()));
            filteredRoutesRewardPoints.add(String.valueOf(route.getRewardPoints()));
        }

        ListView listView_filteredRoutes = (ListView) view.findViewById(R.id.listView_filteredRoutes);

        FilteredListAdapter adapter = new FilteredListAdapter(this.getContext(), filteredRoutesNames, filteredRoutesEstimatedTime, filteredRoutesLength, filteredRoutesRewardPoints);
        listView_filteredRoutes.setAdapter(adapter);



    }

    class FilteredListAdapter extends ArrayAdapter<String> {

        Context context;
        List<String> names;
        List<String> estimatedTimes;
        List<String> lengths;
        List<String> points;

        FilteredListAdapter(Context context, List<String> names, List<String> estimatedTimes, List<String> lengths, List<String> points) {
            super(context, R.layout.item_route_search, R.id.routeName, names);

            this.context = context;
            this.names = names;
            this.estimatedTimes = estimatedTimes;
            this.lengths = lengths;
            this.points = points;

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

            myNames.setText(names.get(position));
            myEstimatedTimes.setText(estimatedTimes.get(position));
            myLengths.setText(lengths.get(position));
            myPoints.setText(points.get(position));

            return row;
        }
    }
}