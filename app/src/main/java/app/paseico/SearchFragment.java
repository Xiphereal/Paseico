package app.paseico;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    EditText et_keyWord;
    EditText et_numberOfPOI;
    EditText et_minimumOfPoints;
    Spinner spinner_theme;
    Spinner spinner_length;
    Spinner spinner_estimatedTime;
    List<String> keyWords;
    String themeOfRoute;
    int numberOfPOI;
    int minimumOfPoints;
    double minimumOfLength;
    double maximumOfLength;
    double minimumTime;
    double maximumTime;
    List<Route> routeList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View fragmentSearchLayout = inflater.inflate(R.layout.fragment_search, container, false);

        et_keyWord = fragmentSearchLayout.findViewById(R.id.editText_keyWord);
        et_numberOfPOI = fragmentSearchLayout.findViewById(R.id.editText_minimumNumberOfPOI);
        et_minimumOfPoints = fragmentSearchLayout.findViewById(R.id.editText_minimumOfPoints);
        spinner_theme = fragmentSearchLayout.findViewById(R.id.spinner_route_theme);
        spinner_length = fragmentSearchLayout.findViewById(R.id.spinner_route_length);
        spinner_estimatedTime = fragmentSearchLayout.findViewById(R.id.spinner_route_estimated_time);

        routeList = new ArrayList<>();

        return fragmentSearchLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_search).setOnClickListener(view1 -> {
            assignValueOfFilterVariables();

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            CollectionReference routesReference = database.collection("route");

            if (themeOfRoute == null || themeOfRoute != getString(R.string.default_spinner_choice)) {
                routesReference.whereEqualTo("theme", themeOfRoute)
                        .whereGreaterThanOrEqualTo("rewardPoints", minimumOfPoints)
                        .get().addOnCompleteListener(task -> {

                    Log.d("RutaTheme", "task is " + task.isSuccessful());

                    if (task.isSuccessful()) {
                        filterByLengthEstimatedTimePointsPOIAndKeyWords(task);

                        Route[] filteredRoutes = new Route[routeList.size()];
                        routeList.toArray(filteredRoutes);

                        NavDirections action = SearchFragmentDirections.actionSearchFragmentToRouteListFragment(filteredRoutes);
                        NavHostFragment.findNavController(SearchFragment.this)
                                .navigate(action);
                    } else {
                        Log.d("Ruta error", "Error getting documents: ", task.getException());
                    }
                });
            } else {
                routesReference.whereGreaterThanOrEqualTo("rewardPoints", minimumOfPoints)
                        .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("RutaNoTheme", "task is " + task.isSuccessful());

                        filterByLengthEstimatedTimePointsPOIAndKeyWords(task);

                        Route[] filteredRoutes = new Route[routeList.size()];
                        routeList.toArray(filteredRoutes);

                        NavDirections action = SearchFragmentDirections.actionSearchFragmentToRouteListFragment(filteredRoutes);
                        NavHostFragment.findNavController(SearchFragment.this)
                                .navigate(action);
                    } else {
                        Log.d("Ruta Error", "Error getting documents: ", task.getException());
                    }
                });
            }
        });
    }

    private void assignValueOfFilterVariables() {
        if (et_numberOfPOI.getText().toString() != "")
            numberOfPOI = Integer.parseInt(et_numberOfPOI.getText().toString());
        else
            numberOfPOI = -1;

        if (et_minimumOfPoints.getText().toString() != "")
            minimumOfPoints = Integer.parseInt(et_minimumOfPoints.getText().toString());
        else
            minimumOfPoints = -1;

        keyWords = Arrays.asList(et_keyWord.getText().toString().trim().split("\\s+"));

        themeOfRoute = spinner_theme.getSelectedItem().toString();
        if (themeOfRoute == getString(R.string.no_theme_choice))
            themeOfRoute = null;

        String estimatedTimeRange = spinner_estimatedTime.getSelectedItem().toString();
        if (estimatedTimeRange != getString(R.string.default_spinner_choice)) {
            if (estimatedTimeRange == getString(R.string.lt0_5)) {
                minimumTime = 0;
                maximumTime = 30;
            } else if (estimatedTimeRange == getString(R.string.gt5)) {
                minimumTime = 5 * 60;
                maximumTime = Double.MAX_VALUE;
            } else {
                String[] rangeOfTime = estimatedTimeRange.split("-");
                minimumTime = Double.parseDouble(rangeOfTime[0]) * 60;
                maximumTime = Double.parseDouble(rangeOfTime[1]) * 60;
            }
        } else {
            minimumTime = -Double.MAX_VALUE;
            maximumTime = Double.MAX_VALUE;
        }

        String lengthRange = spinner_length.getSelectedItem().toString();
        if (lengthRange != getString(R.string.default_spinner_choice)) {
            if (lengthRange == getString(R.string.lt1)) {
                minimumOfLength = 0;
                maximumOfLength = 1000;
            } else if (lengthRange == getString(R.string.gt5)) {
                minimumOfLength = 5000;
                maximumOfLength = Double.MAX_VALUE;
            } else {
                String[] rangeOfLength = lengthRange.split("-");
                minimumOfLength = Double.parseDouble(rangeOfLength[0]) * 1000;
                maximumOfLength = Double.parseDouble(rangeOfLength[1]) * 1000;
            }
        } else {
            minimumOfLength = -Double.MAX_VALUE;
            maximumOfLength = Double.MAX_VALUE;
        }
    }

    private void filterByLengthEstimatedTimePointsPOIAndKeyWords(Task<QuerySnapshot> task) {
        Log.d("Ruta2", "task is " + task.isSuccessful());
        Log.d("Ruta4", "tamaño array tras consulta" + ((QuerySnapshot) task.getResult()).size() + "");

        for (QueryDocumentSnapshot document : task.getResult()) {

            String name = document.getData().get("name").toString();
            Object theme_obj = document.getData().get("theme");
            String theme = theme_obj != null ? theme_obj.toString() : null;
            double length = Double.parseDouble(document.getData().get("length").toString());
            double estimatedTime = Double.parseDouble(document.getData().get("estimatedTime").toString());
            int points = Integer.parseInt(document.getData().get("rewardPoints").toString());

            ArrayList<PointOfInterest> pointOfInterests = (ArrayList) document.getData().get("pointsOfInterest");

            Log.d("Ruta3", document.getId() + " => " + document.getData());

            if (length <= maximumOfLength
                    && length >= minimumOfLength
                    && estimatedTime <= maximumTime
                    && estimatedTime >= minimumTime
                    && pointOfInterests.size() >= numberOfPOI) {
                Log.d("RutaDentro", document.getId() + "=>" + document.getData(), task.getException());
                for (String keyword : keyWords) {
                    if (name.contains(keyword)) {
                        Route route = new Route(name, theme, length, estimatedTime, points, pointOfInterests);

                        routeList.add(route);
                        Log.d("bucle", document.getId() + "=>" + document.getData(), task.getException());
                        break;
                    }
                }
            }

        }
        Log.d("Ruta3", "fin for--> longitud" + routeList.size());
    }
}