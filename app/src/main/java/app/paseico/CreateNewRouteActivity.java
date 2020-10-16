package app.paseico;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import app.paseico.data.PointOfInterest;
import app.paseico.data.Route;

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
        createRouteMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLonOfMarker = marker.getPosition();
                pointsSelected.add(new PointOfInterest(latLonOfMarker.latitude,latLonOfMarker.longitude, marker.getId()));
                return true;
            }
        });
    }

}