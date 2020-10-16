package app.paseico.data;

public class PointOfInterest {
    private double latitude;
    private double longitude;
    private String name;

    public PointOfInterest(double lat, double lon, String nam)
    {
        this.latitude = lat;
        this.longitude = lon;
        this.name = nam;
    }
}
