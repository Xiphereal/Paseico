package app.paseico.data;

import android.widget.Toast;

import androidx.annotation.NonNull;
import app.paseico.IUserDao;
import app.paseico.login.RegisterActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class UserDao implements IUserDao {
    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference
    private Router currentRouter = new Router();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser fbusr = firebaseAuth.getCurrentUser();
    private DatabaseReference myActualUserRef;

    public UserDao() {
        try {
            myActualUserRef = myUsersRef.child(fbusr.getUid());
            myActualUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentRouter = snapshot.getValue(Router.class);
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
    public Router getUser() {
        return currentRouter;
    }

    @Override
    public void addGoogleUser(FirebaseUser user, String name) {
        String email = user.getEmail();
        String[] parts = email.split("@");
        String username = parts[0];
        if(username.contains(".")){
            username = username.replace(".","");
        }
        if(username.contains("#")){
            username = username.replace("#","");
        }
        if(username.contains("$")){
            username = username.replace("$","");
        }
        if(username.contains("[")){
            username = username.replace("[","");
        }
        if(username.contains("]")){
            username = username.replace("]","");
        }
        addNewUserToDatabase(user, name, username);
    }

    @Override
    public void addUser(FirebaseUser user, String username, String name, String surname) {
        String fullname = name + " " + surname;

        addNewUserToDatabase(user, fullname, username);
    }

    private void addNewUserToDatabase(FirebaseUser user, String name, String username) {
        Router newUser = new Router(name, username, user.getEmail());
        myUsersRef.child(user.getUid()).setValue(newUser);
    }
}
