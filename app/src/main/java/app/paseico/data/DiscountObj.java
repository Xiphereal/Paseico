package app.paseico.data;

public class DiscountObj {
    private String name;
    private String percentage;
    private int points;


    public DiscountObj(String n, String p, int pts) {
        this.name = n;
        this.percentage = p;
        this.points = pts;
    }
    public String toString(){return this.name + "\n" + this.percentage + "\n" + this.points + " pts.";}
}
