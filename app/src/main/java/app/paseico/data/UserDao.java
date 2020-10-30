package app.paseico.data;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.paseico.IUserDao;
import app.paseico.data.User;

public class UserDao implements IUserDao {
    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public void addGoogleUser(FirebaseUser user, String name) {
        String email = user.getEmail();
        String[] parts = email.split("@");
        String username = parts[0];
        User newUser = new User(name,username,user.getEmail());
        myUsersRef.child(user.getUid()).setValue(newUser);
    }

    @Override
    public void addUser(FirebaseUser user, String username, String name, String surname) {
        String fullname = name + " " + surname;
        User newUser = new User(fullname,username,user.getEmail());
        myUsersRef.child(user.getUid()).setValue(newUser);
    }


}
