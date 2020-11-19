package app.paseico.mainMenu.searchUsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import app.paseico.R;
import app.paseico.data.User;
import app.paseico.mainMenu.profile.ProfileFragment;
import app.paseico.service.FirebaseService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private boolean isFragment;
    private FirebaseUser firebaseUser;
    User actualUser;
    private SearchUserFragment searchUserFragment;

    public UserAdapter(Context context, List<User> users, boolean isFragment) {
        this.context = context;
        this.users = users;
        this.isFragment = isFragment;
    }

    public UserAdapter(Context context, List<User> users, SearchUserFragment searchUserFragment) {
        this.context = context;
        this.users = users;
        this.isFragment = true;
        this.searchUserFragment = searchUserFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User searchedUser = users.get(position);
        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(searchedUser.getUsername());
        holder.fullname.setText(searchedUser.getName());

        FirebaseService.getCurrentUserReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                actualUser = dataSnapshot.getValue(User.class);
                isFollowing(searchedUser.getUsername(), holder.btn_follow);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        firebaseUser = FirebaseService.getCurrentUser();

        holder.itemView.setOnClickListener(view -> {
            if (isFragment) {
                if (!actualUser.getUsername().equals(searchedUser.getUsername())) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", searchedUser.getUsername());
                    editor.apply();
                    searchUserFragment.navigateToNotMyProfileFragment();
                } else {
                    searchUserFragment.navigateToProfileFragment();
                }
            } else {
                if (!actualUser.getUsername().equals(searchedUser.getUsername())) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", searchedUser.getUsername());
                    editor.apply();
                    Fragment mFragment = new NotMyProfileFragment();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, mFragment)
                            .commit();
                } else {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", searchedUser.getUsername());
                    editor.apply();
                    Fragment mFragment = new ProfileFragment();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, mFragment)
                            .commit();
                }
            }
        });

        holder.btn_follow.setOnClickListener(view -> {
            if (holder.btn_follow.getText().toString().equals("follow")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername())
                        .child("following").child(searchedUser.getUsername()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(searchedUser.getUsername())
                        .child("followers").child(actualUser.getUsername()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername())
                        .child("following").child(searchedUser.getUsername()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(searchedUser.getUsername())
                        .child("followers").child(actualUser.getUsername()).removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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

    private void isFollowing(final String searchedUsername, Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(actualUser.getUsername()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (searchedUsername.equals(actualUser.getUsername())) {
                    button.setVisibility(View.GONE);
                } else {
                    if (snapshot.child(searchedUsername).exists()) {
                        button.setText("following");
                    } else {
                        button.setText("follow");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getGetUsernameFromFirebase(String s) {
        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user);
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
