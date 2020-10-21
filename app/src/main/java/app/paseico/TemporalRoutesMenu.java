package app.paseico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TemporalRoutesMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal_routes_menu);

        Button btnStartRoute = findViewById(R.id.buttonTemporalMenuStartRoute);
        btnStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TemporalRoutesMenu.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}