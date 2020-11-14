package app.paseico;

import com.google.firebase.auth.FirebaseUser;

import app.paseico.data.User;

public interface IUserDao {
    User getUser();

    void addGoogleUser(FirebaseUser user, String name);

    void addUser(FirebaseUser user, String username, String name, String surname);
}
