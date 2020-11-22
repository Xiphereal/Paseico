package app.paseico;

import android.location.Location;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class RouteRunnerBase<Polyline> extends FragmentActivity {
    //google map object
    protected GoogleMap mMap;
    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;
    //to get location permissions.
    protected final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    //polyline object
    protected List<Polyline> polylines = null;

    protected ArrayList<String> pointsOfInterestNames = new ArrayList<String>();
    protected ArrayList<LatLng> locations = new ArrayList<LatLng>();
    protected ArrayList<Boolean> isCompleted = new ArrayList<Boolean>();
    int actualPOI = -1;
    int poisLeft = 0;
    RouteRunnerNotOrderedActivity.ArrayAdapterRutas arrayAdapter;
    int rewpoints;

    Location currentDestination;
    ListView listView;
    String nombredeRuta = "Descubriendo Valencia";

    protected app.paseico.data.Route actualRoute;


}
