package app.paseico;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.paseico.data.Route;
import com.google.android.material.textfield.TextInputLayout;

public class ModifyRouteActivity extends AppCompatActivity {
    private TextInputLayout routeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_route);

        Intent retrievedIntent = this.getIntent();
        Bundle retrievedData = retrievedIntent.getExtras();

        Route retrievedRoute = (Route) retrievedData.get("selectedRoute");

        routeName = findViewById(R.id.route_name_textField);
        routeName.setHint(retrievedRoute.getName());
    }
}