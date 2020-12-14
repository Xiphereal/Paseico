package app.paseico.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationPermissionRequester {

    public static final int LOCATION_REQUEST_CODE = 23;

    public static void requestLocationPermission(Activity requester) {
        if (!isCoarseLocationPermissionAlreadyGranted(requester)) {
            ActivityCompat.requestPermissions(requester, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    public static boolean isCoarseLocationPermissionAlreadyGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean didUserGrantCoarseLocationPermission(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
