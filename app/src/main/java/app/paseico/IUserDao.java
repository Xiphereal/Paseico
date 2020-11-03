package app.paseico;

import com.google.firebase.auth.FirebaseUser;

import app.paseico.data.User;


public interface IUserDao {
    public User getUser();
    public void addGoogleUser(FirebaseUser user,String name);
    public void addUser(FirebaseUser user, String username, String name, String surname);

}
