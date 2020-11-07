package app.paseico.data;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.Objects;

public class PointOfInterest implements Serializable {
    private String name;
    private Double latitude;
    private Double longitude;

    public PointOfInterest(){}

    public PointOfInterest(Double lat, Double lon, String name)
    {
        latitude = lat;
        longitude = lon;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointOfInterest that = (PointOfInterest) o;
        return name.equals(that.name) &&
                latitude.equals(that.latitude) &&
                longitude.equals(that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude);
    }

    public String getName(){ return name; }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
}
