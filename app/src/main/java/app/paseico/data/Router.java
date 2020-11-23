package app.paseico.data;

public class Router extends User {
    private int points;
    private boolean boost;
    private String boostExpires;

    /**
     * True if the User has the "new User free Route creation" available, false if it has been already spent.
     */
    private boolean hasFreeRouteCreation;

    public Router(){
        super();
    }

    public Router(String name, String username, String email){
        super(name,username,email);
        this.points = 0;
        this.boost = false;
        this.boostExpires = null;
        this.hasFreeRouteCreation = true;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int pts) {
        this.points += pts;
    }

    public void subtractPoints(int pts) {
        this.points -=  pts;
    }

    public boolean isBoost() {
        return boost;
    }

    public void setBoost(boolean boost) {
        this.boost = boost;
    }

    public String getBoostExpires() {
        return boostExpires;
    }

    // Simply hasFreeRouteCreation would be a better name, but in order to Firebase to properly populate
    // the new instance from a snapshot, the getters & setters must be get<SameFieldName>.
    public boolean getHasFreeRouteCreation() {
        return hasFreeRouteCreation;
    }

    public void setHasFreeRouteCreation(boolean hasFreeRouteCreation) {
        this.hasFreeRouteCreation = hasFreeRouteCreation;
    }
}
