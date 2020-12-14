package app.paseico.data;

public class Discount {
    private String name;
    private int percentage;
    private int points;
    private String organiID;



    private String nodeId;

    public Discount() {}

    public Discount(String n, int p, int pts, String organiID, String node) {
        this.name = n;
        this.percentage = p;
        this.points = pts;
        this.organiID = organiID;
        this.nodeId = node;
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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }


}
