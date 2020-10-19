package app.paseico;

import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;

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

        return fragmentSearchLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Ruta1", "click");
                assignValueOfFilterVariables();


                FirebaseFirestore database = FirebaseFirestore.getInstance();
                CollectionReference routesReference = database.collection("route");
                Log.d("Ruta1", "bd");

                if (themeOfRoute != getString(R.string.default_spinner_choice)) {

                    routesReference.whereEqualTo("theme", themeOfRoute)
                            .whereGreaterThan("points", minimumOfPoints)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Log.d("Ruta1", "listener");
                            Log.d("Ruta1", "task is " + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Log.d("Ruta2", "task is " + task.isSuccessful());
                                Log.d("Ruta3", ((QuerySnapshot)task.getResult()).size() + "");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Ruta3", document.getId() + " => " + document.getData());
                                    Log.d("Ruta3", task.getResult().toString());
                                    if(Double.parseDouble(document.getData().get("length").toString()) <= maximumOfLength
                                        || Double.parseDouble(document.getData().get("length").toString()) >= minimumOfLength
                                        || Double.parseDouble(document.getData().get("estimatedTime").toString()) <= maximumTime
                                        || Double.parseDouble(document.getData().get("estimatedTime").toString()) >= minimumTime
                                        || ((List<PointOfInterest>) document.getData().get("POIs")).size() >= numberOfPOI){
                                        Log.d("Ruta3", document.getId() + "=>" + document.getData(), task.getException());
                                            //routeList.add((Route) document.getData().values()); 
                                    }

                                }
                                Log.d("Ruta3", "fin for");
                            } else {
                                Log.d("routes", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                } else {
                    routesReference.whereArrayContainsAny("name", keyWords)
                            .whereGreaterThan("points", minimumOfPoints)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if((Double) document.getData().get("length") <= maximumOfLength
                                            || (Double) document.getData().get("length") >= minimumOfLength
                                            || (Double) document.getData().get("estimatedTime") <= maximumTime
                                            || (Double) document.getData().get("estimatedTime") >= minimumTime
                                            || ((List<PointOfInterest>) document.getData().get("POIs")).size() >= numberOfPOI){
                                        Log.d("routes", document.getId() + "=>" + document.getData(), task.getException());
                                        routeList.add((Route) document.getData().values());
                                    }
                                }
                            } else {
                                Log.d("routes", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                    //routesReference2.orderByChild("name")
                }

                //NavHostFragment.findNavController(SearchFragment.this)
                       // .navigate(R.id.action_SearchFragment_to_RouteListFragment);
            }
        });
    }

    private void assignValueOfFilterVariables() {
        if (et_numberOfPOI.getText().toString() != "") {
            numberOfPOI = Integer.parseInt(et_numberOfPOI.getText().toString());
        } else {
            numberOfPOI = -1;
        }

        if (et_minimumOfPoints.getText().toString() != "") {
            minimumOfPoints = Integer.parseInt(et_minimumOfPoints.getText().toString());
        } else {
            minimumOfPoints = -1;
        }
        Log.d("Ruta1", "antes de keyword");
        keyWords = Arrays.asList(et_keyWord.getText().toString().trim().split("\\s+"));
        Log.d("Ruta1", "despues de keyword");
        themeOfRoute = spinner_theme.getSelectedItem().toString();

        String estimatedTimeRange = spinner_estimatedTime.getSelectedItem().toString();
        if (estimatedTimeRange != getString(R.string.default_spinner_choice)) {
            if (estimatedTimeRange == getString(R.string.lt0_5)) {
                minimumTime = 0;
                maximumTime = 30;
            } else if (estimatedTimeRange ==getString(R.string.gt5)) {
                minimumTime = 5*60;
                maximumTime = Double.MAX_VALUE;
            } else {
                String [] rangeOfTime = estimatedTimeRange.split("-");
                minimumTime = Double.parseDouble(rangeOfTime[0]) * 60;
                maximumTime = Double.parseDouble(rangeOfTime[1]) * 60;
            }
        } else {
            minimumTime = Double.MIN_VALUE;
            maximumTime = Double.MAX_VALUE;
        }

        String lengthRange = spinner_length.getSelectedItem().toString();
        if (lengthRange != getString(R.string.default_spinner_choice)) {
            if (lengthRange == getString(R.string.lt1)) {
                minimumTime = 0;
                maximumTime = 1000;
            } else if (lengthRange ==getString(R.string.gt5)) {
                minimumTime = 5000;
                maximumTime = Double.MAX_VALUE;
            } else {
                String [] rangeOfTime = lengthRange.split("-");
                minimumTime = Double.parseDouble(rangeOfTime[0]) * 1000;
                maximumTime = Double.parseDouble(rangeOfTime[1]) * 1000;
            }
        } else {
            minimumTime = Double.MIN_VALUE;
            maximumTime = Double.MAX_VALUE;
        }
    }
}