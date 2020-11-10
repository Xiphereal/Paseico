package app.paseico.ui.searchUsers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import app.paseico.MainActivity;
import app.paseico.MainMenuActivity;
import app.paseico.R;
import app.paseico.SearchFragment;
import app.paseico.SearchFragmentDirections;
import app.paseico.data.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isfragment;
    private FirebaseUser firebaseUser;
    private String usernameFirebase;
    User actualUser;
    private SearchUserFragment searchUserFragment;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isfragment)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isfragment = isfragment;
    }

    public UserAdapter(Context mContext, List<User> mUsers, SearchUserFragment searchUserFragment)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isfragment = true;
        this.searchUserFragment = searchUserFragment;
    }




    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());
        //Glide.with(mContext).load("@drawable/ic_user").into(holder.image_profile);
        //Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                actualUser = dataSnapshot.getValue(User.class);
                isFollowing(user.getUsername(), holder.btn_follow);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (isfragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUsername());
                    editor.apply();

                    searchUserFragment.navigateToNotMyProfileFragment();
                } else{
                    //Intent intent = new Intent(mContext, MainMenuActivity.class);
                    //mContext.startActivity(intent);
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUsername());
                    editor.apply();
                      Fragment mFragment = null;
                        mFragment = new NotMyProfileFragment();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, mFragment)
                            .commit();
                }
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(holder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername())
                            .child("following").child(user.getUsername()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUsername())
                            .child("followers").child(actualUser.getUsername()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername())
                            .child("following").child(user.getUsername()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUsername())
                            .child("followers").child(actualUser.getUsername()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()){
                    button.setText("following");
                }else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGetUsernameFromFirebase(String s){
        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUsers.add(user);
                usernameFirebase = user.getUsername();
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
