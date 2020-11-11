package app.paseico.data;

public class User {
    private String name;
    private String email;
    private String username;
    private String password;
    private int points;
    private boolean boost;
    private String boostExpires;

    /**
     * True if the User has the "new User free Route creation" available, false if it has been already spent.
     */
    private boolean hasFreeRouteCreation;

    public User() {
    }

    public User(String name, String username, String email) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.points = 0;
        this.boost = false;
        this.boostExpires = null;
        this.hasFreeRouteCreation = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int pts) {
        this.points = this.points + pts;
    }

    public void subtractPoints(int pts) {
        this.points = this.points - pts;
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

    public boolean hasFreeRouteCreation() {
        return hasFreeRouteCreation;
    }

    public void setHasFreeRouteCreation(boolean hasFreeRouteCreation) {
        this.hasFreeRouteCreation = hasFreeRouteCreation;
    }
}

