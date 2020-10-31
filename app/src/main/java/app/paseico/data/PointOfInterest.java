package app.paseico.data;

import com.google.android.gms.maps.model.Marker;


public class PointOfInterest {
    private Marker googleMarker;
    private String name;

    public PointOfInterest(Marker googleMarker, String name)
    {
        this.googleMarker = googleMarker;
        this.name = name;

    }


    public Marker getGoogleMarker() {
        return googleMarker;
    }

    public String getName(){
        return name;
    }

    public double getLatitude() {
        return this.googleMarker.getPosition().latitude;
    }
    public double getLongitude() {
        return this.googleMarker.getPosition().longitude;
    }
}
