package app.paseico;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class SearchFragment extends Fragment {

    EditText et_keyWord;
    EditText et_numberOfPOI;
    EditText et_minimumOfPoints;
    Spinner spinner_theme;
    Spinner spinner_length;
    Spinner spinner_estimatedTime;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View fragmentSearchLayout = inflater.inflate(R.layout.fragment_search, container, false);

        et_keyWord = fragmentSearchLayout.findViewById(R.id.editText_keyWord);
        et_numberOfPOI = fragmentSearchLayout.findViewById(R.id.editText_numberOfPOI);
        et_minimumOfPoints = fragmentSearchLayout.findViewById(R.id.editText_minimumOfPoints);
        spinner_theme = fragmentSearchLayout.findViewById(R.id.spinner_route_theme);
        spinner_length = fragmentSearchLayout.findViewById(R.id.spinner_route_length);
        spinner_estimatedTime = fragmentSearchLayout.findViewById(R.id.spinner_route_estimated_time);

        return fragmentSearchLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(SearchFragment.this)
                        .navigate(R.id.action_SearchFragment_to_RouteListFragment);
            }
        });
    }
}