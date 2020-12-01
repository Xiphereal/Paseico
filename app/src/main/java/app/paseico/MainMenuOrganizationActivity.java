package app.paseico;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import app.paseico.mainMenu.userCreatedRoutes.CreateNewRouteActivity;

public class MainMenuOrganizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_organization);
        
        registerCreateNewOrganizationRouteButton();
    }

    private void registerCreateNewOrganizationRouteButton() {
        final Button createOrganizationRoute = findViewById(R.id.buttonCreateRouteOrg);
        createOrganizationRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNewRouteIntent = new Intent(getApplicationContext(), CreateNewRouteActivity.class);
                startActivity(createNewRouteIntent);
            }
        });
    }
}