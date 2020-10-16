package app.paseico;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import app.paseico.data.PointOfInterest;

public class CreateNewRouteActivity extends AppCompatActivity {

    private static GoogleMap createRouteMap;
    private List<PointOfInterest> pointsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);
        createRouteMap = MainMapActivity.getMap();
    }

    private void addPointOfInterestToRoute(){
        createRouteMap.setOnMarkerClickListener(marker -> {
            pointsSelected.add(new PointOfInterest(marker, marker.getId()));
            return true;
        });
    }

}