package app.paseico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RouteFinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finished);

        Button goToMenu = findViewById(R.id.buttonTemporalMenuStartRoute);
        goToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteFinishedActivity.this, TemporalRoutesMenu.class);
                startActivity(intent);
                finish();
            }
        });
    }
}