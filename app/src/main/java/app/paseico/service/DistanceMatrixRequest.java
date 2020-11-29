package app.paseico.service;

import android.content.Context;
import app.paseico.R;

public class DistanceMatrixRequest {

    private String urlRequest = "https://maps.googleapis.com/maps/api/distancematrix/";

    private final Context context;

    public DistanceMatrixRequest(Context context) {
        this.context = context;
    }

    public void send() {
        urlRequest += "&key=" + context.getString(R.string.google_api_key);

        // TODO: Send the request to Google.
    }
}
