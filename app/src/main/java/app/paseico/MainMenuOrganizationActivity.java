package app.paseico;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import app.paseico.login.LogInActivity;
import app.paseico.mainMenu.userCreatedRoutes.CreateNewRouteActivity;

public class MainMenuOrganizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_organization);
        
        registerCreateNewOrganizationRouteButton();
        createLogOutOrgButton();
    }

    private void registerCreateNewOrganizationRouteButton() {
        final Button createOrganizationRoute = findViewById(R.id.buttonCreateRouteOrg);
        createOrganizationRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNewRouteIntent = new Intent(getApplicationContext(), CreateNewRouteActivity.class);
                createNewRouteIntent.putExtra("organization", true);
                startActivity(createNewRouteIntent);
            }
        });
    }

    public void createLogOutOrgButton() {
        final Button closeSessionOrg = findViewById(R.id.LogOutOrg);
        closeSessionOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainMenuOrganizationActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}