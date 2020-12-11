package app.paseico;

import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import app.paseico.service.FirebaseService;

public class MainMenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setSupportActionBar(findViewById(R.id.toolbar));

        setDrawerLayout();

        setUpNavigationUi();

        setEmailInNavHeader();
    }

    private void setDrawerLayout() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.Profile, R.id.UserCreatedRoutes, R.id.RouteSearchFragment, R.id.nav_searchUsers, R.id.nav_marketplace)
                .setDrawerLayout(drawer)
                .build();
    }

    private void setEmailInNavHeader(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = drawer.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        TextView textViewEmail = headerView.findViewById(R.id.ruterEmail_textView);
        textViewEmail.setText(userEmail);
    }

    private void setUpNavigationUi() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}