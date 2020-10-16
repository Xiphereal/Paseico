package app.paseico.data;

import com.google.android.gms.maps.model.Marker;

public class PointOfInterest {
    private Marker googleMarker;
    private String name;

    public PointOfInterest(Marker googleMarker, String nam)
    {
        this.googleMarker = googleMarker;
        this.name = nam;
    }
}
