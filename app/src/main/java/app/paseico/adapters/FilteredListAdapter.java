package app.paseico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import app.paseico.R;

public class FilteredListAdapter extends ArrayAdapter<String> {

    Context context;
    List<String> names;
    List<String> estimatedHours;
    List<String> estimatedMinutes;
    List<String> kms;
    List<String> meters;
    List<String> points;
    List<Integer> icons;
    List<String> areOrdered;


    public FilteredListAdapter(Context context, List<String> names, List<String> estimatedHours, List<String> estimatedMinutes, List<String> kms, List<String> meters, List<String> points, List<Integer> icons, List<String> areOrdered) {
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
