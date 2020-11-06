package app.paseico.data;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.paseico.IUserDao;
import app.paseico.data.User;

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

        } catch(Exception e){}
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        date.setDate(date.getDate() -1);
        String strDate = dateFormat.format(date).toString();
        User newUser = new User(name,username,user.getEmail(),strDate);
        myUsersRef.child(user.getUid()).setValue(newUser);
    }

    @Override
    public void addUser(FirebaseUser user, String username, String name, String surname) {
        String fullname = name + " " + surname;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        date.setDate(date.getDate() -1);
        String strDate = dateFormat.format(date).toString();
        User newUser = new User(fullname,username,user.getEmail(),strDate);
        myUsersRef.child(user.getUid()).setValue(newUser);
    }



}
