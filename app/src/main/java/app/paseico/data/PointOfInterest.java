package app.paseico.data;

import com.google.android.gms.maps.model.Marker;

public class PointOfInterest {
    private Marker googleMarker;

    public Marker getGoogleMarker() {
        return googleMarker;
    }

    private String name;

    public PointOfInterest(Marker googleMarker, String name)
    {
        this.googleMarker = googleMarker;
        this.name = name;
    }

    public Marker getGoogleMarker() {
        return googleMarker;
    }
}
