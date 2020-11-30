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
import androidx.navigation.fragment.NavHostFragment;

import app.paseico.CategoryManager;
import app.paseico.R;
import app.paseico.RouteInfModifyActivity;
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
        List<String> filteredRoutesEstimatedTime = new ArrayList<>();
        List<String> filteredRoutesLength = new ArrayList<>();
        List<String> filteredRoutesRewardPoints = new ArrayList<>();

        List<Integer> filteredRoutesIcons = new ArrayList<>();


        for (Route route : filteredRoutes) {
            filteredRoutesNames.add(route.getName());
            filteredRoutesEstimatedTime.add(String.valueOf(route.getEstimatedTime()));
            filteredRoutesLength.add(String.valueOf(route.getLength()));
            filteredRoutesRewardPoints.add(String.valueOf(route.getRewardPoints()));

            //Obtain route theme
            String RouteCategory = route.getTheme();
            System.out.println("categoria " + RouteCategory);
            int index = CategoryManager.ConvertCategoryToIntDrawable(RouteCategory);
            System.out.println("indice " + index);
            filteredRoutesIcons.add(index);

        }

        ListView listView_filteredRoutes = (ListView) view.findViewById(R.id.listView_filteredRoutes);

        FilteredListAdapter adapter = new FilteredListAdapter(this.getContext(), filteredRoutesNames, filteredRoutesEstimatedTime, filteredRoutesLength, filteredRoutesRewardPoints, filteredRoutesIcons);
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

    class FilteredListAdapter extends ArrayAdapter<String> {

        Context context;
        List<String> names;
        List<String> estimatedTimes;
        List<String> lengths;
        List<String> points;
        List<Integer> icons;


        FilteredListAdapter(Context context, List<String> names, List<String> estimatedTimes, List<String> lengths, List<String> points, List<Integer> icons) {
            super(context, R.layout.item_route_search, R.id.routeName, names);

            this.context = context;
            this.names = names;
            this.estimatedTimes = estimatedTimes;
            this.lengths = lengths;
            this.points = points;
            this.icons = icons;
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

            ImageView ListViewImage = (ImageView) row.findViewById(R.id.imageViewIcon);

            myNames.setText(names.get(position));
            myEstimatedTimes.setText(estimatedTimes.get(position));
            myLengths.setText(lengths.get(position));
            myPoints.setText(points.get(position));

           ListViewImage.setImageResource(icons.get(position));

            return row;
        }


    }

}
