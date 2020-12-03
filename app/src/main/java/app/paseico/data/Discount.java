package app.paseico.data;

public class Discount {
    private String name;
    private int percentage;
    private int points;
    private String organiID;

    public Discount() {}

    public Discount(String n, int p, int pts, String organiID) {
        this.name = n;
        this.percentage = p;
        this.points = pts;
        this.organiID = organiID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getOrganiID() {
        return organiID;
    }

    public void setOrganiID(String organiID) {
        this.organiID = organiID;
    }



}
