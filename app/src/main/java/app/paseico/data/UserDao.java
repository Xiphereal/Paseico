package app.paseico.data;

import androidx.annotation.NonNull;
import app.paseico.IUserDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class UserDao implements IUserDao {
    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference
    private User user = new User();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser fbusr = firebaseAuth.getCurrentUser();
    private DatabaseReference myActualUserRef;

    public UserDao() {
        try {
            myActualUserRef = myUsersRef.child(fbusr.getUid());
            myActualUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The db connection failed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void addGoogleUser(FirebaseUser user, String name) {
        String email = user.getEmail();
        String[] parts = email.split("@");
        String username = parts[0];

        addNewUserToDatabase(user, name, username);
    }

    @Override
    public void addUser(FirebaseUser user, String username, String name, String surname) {
        String fullname = name + " " + surname;

        addNewUserToDatabase(user, fullname, username);
    }

    private void addNewUserToDatabase(FirebaseUser user, String name, String username) {
        User newUser = new User(name, username, user.getEmail());
        myUsersRef.child(user.getUid()).setValue(newUser);
    }
}
