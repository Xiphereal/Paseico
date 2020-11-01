package app.paseico.mainMenu.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import app.paseico.R;
import app.paseico.login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class profileFragment extends Fragment {

    private profileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(profileViewModel.class);

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        profileViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        super.onCreate(savedInstanceState);

        Button btnLogOut = root.findViewById(R.id.buttonLogOut);
        btnLogOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
            getActivity().finish();
        });


        return root;
    }
}