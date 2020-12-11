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

    class FilteredListAdapter extends ArrayAdapter<String> {

        Context context;
        List<String> names;
        List<String> estimatedHours;
        List<String> estimatedMinutes;
        List<String> kms;
        List<String> meters;
        List<String> points;
        List<Integer> icons;
        List<String> areOrdered;


        FilteredListAdapter(Context context, List<String> names, List<String> estimatedHours, List<String> estimatedMinutes, List<String> kms, List<String> meters, List<String> points, List<Integer> icons, List<String> areOrdered) {
            super(context, R.layout.item_route_search, R.id.routeName, names);

            this.context = context;
            this.names = names;
            this.estimatedHours = estimatedHours;
            this.estimatedMinutes = estimatedMinutes;
            this.kms = kms;
            this.meters = meters;
            this.points = points;
            this.icons = icons;
            this.areOrdered = areOrdered;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_route_search, parent, false);

            TextView myNames = row.findViewById(R.id.routeName);
            TextView myEstimatedTimes = row.findViewById(R.id.routeDuration);
            TextView myEstimatedMinutes = row.findViewById(R.id.routeMinutes);
            TextView myKms = row.findViewById(R.id.routeLenght);
            TextView myMeters = row.findViewById(R.id.meters_textView);
            TextView myPoints = row.findViewById(R.id.routeReward);
            TextView orderedRoute = row.findViewById(R.id.textView_orderedRouteResult);


            ImageView ListViewImage = (ImageView) row.findViewById(R.id.imageViewIcon);

            myNames.setText(names.get(position));
            myEstimatedTimes.setText(estimatedHours.get(position));
            myEstimatedMinutes.setText(estimatedMinutes.get(position));
            myKms.setText(kms.get(position));
            myMeters.setText(meters.get(position));
            myPoints.setText(points.get(position));

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
