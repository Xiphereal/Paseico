package app.paseico.ui.routes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import app.paseico.R;

public class RoutesFragment extends Fragment {

    private RoutesViewModel routesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        routesViewModel = new ViewModelProvider(this).get(RoutesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_routes, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);

        routesViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        return root;
    }
}