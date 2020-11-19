package app.paseico.data;

public class Organization extends User{
    private String nif;

    public Organization(){
        super();
    }

    public Organization(String name, String email, String username, String nif){
        super(name,email,username);
        this.nif = nif;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

}
