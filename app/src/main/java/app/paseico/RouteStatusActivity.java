package app.paseico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RouteStatusActivity extends AppCompatActivity {
    static ArrayList<String> pointsOfInterests = new ArrayList<String>();
    //static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_status);

        ListView listView = findViewById(R.id.listView);
        pointsOfInterests.add("Current location");
        pointsOfInterests.add("POI 1");
        pointsOfInterests.add("POI 2");
        //locations.add(new LatLng(0,0));

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pointsOfInterests);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), RoutingActivity.class);
                intent.putExtra("placeNumber",i);

                startActivity(intent);
            }
        });
    }
}