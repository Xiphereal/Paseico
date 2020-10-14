package app.paseico;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDao implements IUserDao {
    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public void addGoogleUser(FirebaseUser user) {
        String email = user.getEmail();
        String[] parts = email.split("@");
        String username = parts[0];
        User newUser = new User(user.getDisplayName(),username,user.getEmail());
        myUsersRef.child(user.getUid()).setValue(newUser);
    }
}
