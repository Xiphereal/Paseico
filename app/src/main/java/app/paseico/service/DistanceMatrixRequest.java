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

        urlRequest = new StringBuilder("http://maps.googleapis.com/maps/api/distancematrix/json?");
    }

    /**
     * Appends optional parameters to the request just after the '/responseFormat?'. A "&" is appended at the end
     * for allowing consequent aditional parameters aditions or the adition of the {@link app.paseico.data.Route}
     * {@link PointOfInterest}.
     *
     * @param optionalParameter The expected format is "parameterName=parameterValues".
     */
    public DistanceMatrixRequest addOptionalParameter(String optionalParameter) {
        urlRequest.append(optionalParameter);
        urlRequest.append("&");

        return this;
    }

    /**
     * Adds the first {@link PointOfInterest} in the passed collection as an origin, the last as the
     * destination and any other {@link PointOfInterest} as a waypoint for the {@link app.paseico.data.Route}.
     */
    public DistanceMatrixRequest addPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        urlRequest.append("origins=");
        appendPointOfInterest(pointsOfInterest.get(0));

        urlRequest.append("&waypoints=");
        for (int i = 1; i < pointsOfInterest.size() - 1; i++) {
            appendPointOfInterest(pointsOfInterest.get(i));
        }

        urlRequest.append("&destinations=");
        appendPointOfInterest(pointsOfInterest.get(pointsOfInterest.size() - 1));

        return this;
    }

    private void appendPointOfInterest(PointOfInterest poi) {
        urlRequest.append(poi.getLatitude());
        urlRequest.append(",");
        urlRequest.append(poi.getLongitude());
    }

    public void send() {
        urlRequest.append("&key=").append(context.getString(R.string.google_api_key));

        String response = HttpService.executeGet(urlRequest.toString());

        System.out.println(response);
    }
}
