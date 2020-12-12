package app.paseico.mainMenu.searcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.paseico.R;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    List<String> names;
    List<String> estimatedHours;
    List<String> estimatedMinutes;
    List<String> kms;
    List<String> meters;
    List<String> points;
    List<Integer> icons;
    List<String> areOrdered;
    List<String> organization;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public MyRecyclerViewAdapter(Context context, List<String> names, List<String> estimatedHours, List<String> estimatedMinutes,List<String> kms, List<String> meters, List<String> points,
                                 List<Integer> icons, List<String> areOrdered, List<String> organization){
        this.mInflater = LayoutInflater.from(context);
        this.names = names;
        this.estimatedHours = estimatedHours;
        this.estimatedMinutes = estimatedMinutes;
        this.kms = kms;
        this.meters = meters;
        this.points = points;
        this.icons = icons;
        this.areOrdered = areOrdered;
        this.organization = organization;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_route_search, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.myNames.setText(names.get(position));
        holder.myEstimatedTimes.setText(estimatedHours.get(position));
        holder.myMinutes.setText(estimatedMinutes.get(position));
        holder.myLengths.setText(kms.get(position));
        holder.myMeters.setText(meters.get(position));
        holder.myPoints.setText(points.get(position));
        holder.organizationName.setText(organization.get(position));

        String isOrdered = areOrdered.get(position);
        if (isOrdered.equals("0")){
            isOrdered = "No";
        } else {
            isOrdered = "Sí";
        }

        holder.orderedRoute.setText(isOrdered);

        holder.ListViewImage.setImageResource(icons.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View myView;
        TextView myNames;
        TextView myEstimatedTimes;
        TextView myMinutes;
        TextView myLengths;
        TextView myMeters;
        TextView myPoints;
        TextView orderedRoute;
        TextView organizationName;
        ImageView ListViewImage;

        ViewHolder(View itemView) {
            super(itemView);
             myNames = itemView.findViewById(R.id.routeName);
             myEstimatedTimes = itemView.findViewById(R.id.routeDuration);
             myMinutes = itemView.findViewById(R.id.routeMinutes);
             myLengths = itemView.findViewById(R.id.routeLenght);
             myMeters = itemView.findViewById(R.id.meters_textView);
             myPoints = itemView.findViewById(R.id.routeReward);
             orderedRoute = itemView.findViewById(R.id.textView_orderedRouteResult);
             organizationName = itemView.findViewById(R.id.organizationNameText);
             ListViewImage = (ImageView) itemView.findViewById(R.id.imageViewIcon);
             itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return names.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
