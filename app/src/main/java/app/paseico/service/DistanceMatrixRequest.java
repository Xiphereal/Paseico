package app.paseico.service;

import android.content.Context;
import app.paseico.R;
import app.paseico.data.PointOfInterest;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DistanceMatrixRequest {

    private StringBuilder urlRequest;

    private final Context context;

    @NotNull
    public DistanceMatrixRequest(Context context) {
        this.context = context;

        urlRequest = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");
    }

    /**
     * Adds the first {@link PointOfInterest} in the passed collection as an origin, the last as the
     * destination and any other {@link PointOfInterest} as a waypoint for the route.
     */
    public DistanceMatrixRequest addPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        urlRequest.append("origins=");
        appendPointOfInterest(pointsOfInterest.get(0));

        urlRequest.append("&waypoints=");
        for (int i = 1; i < pointsOfInterest.size() - 1; i++) {
            appendPointOfInterest(pointsOfInterest.get(i));
        }

        urlRequest.append("&destinations=");
        appendPointOfInterest(pointsOfInterest.get(pointsOfInterest.size()));

        return this;
    }

    private void appendPointOfInterest(PointOfInterest poi) {
        urlRequest.append(poi.getLatitude());
        urlRequest.append(",");
        urlRequest.append(poi.getLongitude());
    }

    public void send() {
        urlRequest.append("&key=").append(context.getString(R.string.google_api_key));

        // TODO: Send the request to Google.
    }
}
