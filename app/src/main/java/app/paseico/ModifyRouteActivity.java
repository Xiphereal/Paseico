package app.paseico;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.paseico.data.Route;

public class ModifyRouteActivity extends RouteInformationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_modify_route);

        Route route = setFilteredInformation();

        clickedBack();

        clickedStartRoute(route);

        findViewById(R.id.btn_routeInfo_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}