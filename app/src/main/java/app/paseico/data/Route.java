package app.paseico.data;

import java.util.List;

public class Route {
    private String name;
    private List<PointOfInterestPaseico> pointsOfInterest;

    public Route(String name, List<PointOfInterestPaseico> pois){
        this.name = name;
        this.pointsOfInterest = pois;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PointOfInterestPaseico> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterestPaseico> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }
}
