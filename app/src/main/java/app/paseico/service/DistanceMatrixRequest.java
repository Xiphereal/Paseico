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

    public void send() {
        urlRequest.append("&key=").append(context.getString(R.string.google_api_key));

        // TODO: Send the request to Google.
    }
}
