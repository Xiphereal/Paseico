package app.paseico.service;

import android.content.Context;
import app.paseico.R;
import app.paseico.data.PointOfInterest;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

public class DistanceMatrixRequest {

    private StringBuilder urlRequest;

    private String response;
    private double estimatedDistanceInMeters;
    private double estimatedDurationInMinutes;

    private final Context context;

    @NotNull
    public DistanceMatrixRequest(Context context) {
        this.context = context;

        urlRequest = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");
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

        if (pointsOfInterest.size() > 2) {
            urlRequest.append("&waypoints=");
            for (int i = 1; i < pointsOfInterest.size() - 1; i++) {
                appendPointOfInterest(pointsOfInterest.get(i));
            }
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

        // TODO: Thread synchronization to get the response synchronously.
        new Thread(() -> {
            response = HttpsService.executeGet(urlRequest.toString());

            extractResultsFromJsonResponse();

        }).start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void extractResultsFromJsonResponse() {
        try {
            JSONParser jsonparser = new JSONParser();
            JSONObject entireJsonFile = (JSONObject) jsonparser.parse(response);

            JSONArray rows = (JSONArray) entireJsonFile.get("rows");
            Object[] rowsArray = rows.toArray();

            JSONArray elements = (JSONArray) ((JSONObject) rowsArray[0]).get("elements");
            Object[] elementsArray = elements.toArray();

            JSONObject distance = (JSONObject) ((JSONObject) elementsArray[0]).get("distance");
            estimatedDistanceInMeters = ((Long) distance.get("value")).intValue();

            JSONObject duration = (JSONObject) ((JSONObject) elementsArray[0]).get("duration");
            estimatedDurationInMinutes = ((Long) duration.get("value")).intValue();

            if (estimatedDurationInMinutes != 0)
                estimatedDurationInMinutes /= 60;

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public double getRouteEstimatedDuration() {
        return estimatedDurationInMinutes;
    }

    public double getRouteEstimatedDistance() {
        return estimatedDistanceInMeters;
    }
}
