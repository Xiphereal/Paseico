package app.paseico.data;

import java.io.Serializable;
import java.util.Objects;

public class PointOfInterest implements Serializable {
    private String name;
    private Double latitude;
    private Double longitude;
    private boolean createdByUser;

    public PointOfInterest() {
    }

    public PointOfInterest(Double lat, Double lon, String name) {
        latitude = lat;
        longitude = lon;
        this.name = name;
    }

    public PointOfInterest(Double lat, Double lon, String name, boolean createdByUser) {
        latitude = lat;
        longitude = lon;
        this.name = name;
        this.createdByUser = createdByUser;
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

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean wasCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(boolean createdByUser) {
        this.createdByUser = createdByUser;
    }
}
