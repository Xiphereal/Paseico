package app.paseico.data;

import java.util.List;

public class Route {
    private String name;
    private String theme;   // TODO: The value of theme should be a constant.
    private double length;  // Meters.
    private double estimatedTime;  // Minutes
    private int rewardPoints;     // Points earned when the route is completed.
    private List<PointOfInterest> pointsOfInterest;

    //TODO: Route should have a reference to the author User, in order to know which user created the Route.
    public Route(String name, List<PointOfInterest> pointOfInterests) {
        this.name = name;
        this.pointsOfInterest = pointOfInterests;
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

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }
}
