package app.paseico.data;


import java.util.List;

public class Route {
    private String name;
    private String theme;   // TODO :The value of theme should be a constant.
    private double length;  // Meters.
    private double estimatedTime;  // Minutes
    private int points;     // Points earned when the route is completed.
    private List<PointOfInterest> pointsOfInterest;

    public Route(String name, List<PointOfInterest> pointOfInterests) {
        this.name = name;
        this.pointsOfInterest = pointOfInterests;
    }

    public Route(String name, String theme, double length, double estimatedTime, int points, List<PointOfInterest> pointsOfInterest) {
        this.name = name;
        this.theme = theme;
        this.length = length;
        this.estimatedTime = estimatedTime;
        this.points = points;
        this.pointsOfInterest = pointsOfInterest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }
}
