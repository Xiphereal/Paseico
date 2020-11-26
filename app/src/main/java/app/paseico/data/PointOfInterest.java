package app.paseico.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

public class PointOfInterest implements Serializable, Parcelable {
    private String name;
    private Double latitude;
    private Double longitude;
    private boolean createdByUser;

    public static final Creator<PointOfInterest> CREATOR = new Creator<PointOfInterest>() {
        @Override
        public PointOfInterest createFromParcel(Parcel in) {
            return new PointOfInterest(in);
        }

        @Override
        public PointOfInterest[] newArray(int size) {
            return new PointOfInterest[size];
        }
    };

    public PointOfInterest() {
    }

    protected PointOfInterest(Parcel in) {
        name = in.readString();

        if (in.readByte() == 0)
            latitude = null;
        else
            latitude = in.readDouble();

        if (in.readByte() == 0)
            longitude = null;
        else
            longitude = in.readDouble();

        createdByUser = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);

        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }

        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }

        dest.writeByte((byte) (createdByUser ? 1 : 0));
    }
}
//