package app.paseico.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class User {
    private String name;
    private String email;
    private String username;
    private String password;
    private int points;
    private boolean boost;
    private String boostExpires;
    private String lastFreeAd;
    private String imageurl;
    private String id;

    public User(){

    }


    public User(String name, String username, String email, String lastFreeAd, String imageurl, String id) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.points = 0;
        this.boost = false;
        this.boostExpires = null;
        this.lastFreeAd = lastFreeAd;
        this.imageurl = imageurl;
        this.id = id;

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

    public String getImageurl() { return imageurl; }

    public void setImageurl(String imageurl){ this.imageurl = imageurl; }

    public String getId(){ return id;}

    public void setId(String id){ this.id = id; }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int pts) {this.points = this.points + pts;}

    public void subtractPoints(int pts) {this.points = this.points - pts;}

    public boolean isBoost() {
        return boost;
    }

    public void setBoost(boolean boost) {
        this.boost = boost;
    }

    public String getLastFreeAd(){return lastFreeAd;}
    public String getBoostExpires(){return boostExpires;}
}

