package pl.polsl.blissapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.ui.settings.ThemeManager;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Install splash screen only on fresh launch
        if (savedInstanceState == null) {
            SplashScreen.installSplashScreen(this);
        }

        // 2. Apply the saved theme before super.onCreate
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        // 3. Enable edge-to-edge (draw behind status bar)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        NavigationView navigationView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.nav_writer);
            topLevelDestinations.add(R.id.nav_dictionary);
            topLevelDestinations.add(R.id.nav_alchemy);

            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                    .setOpenableLayout(drawerLayout)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            // Handle navigation item clicks (including Settings)
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_settings) {
                    navController.navigate(id);
                    drawerLayout.closeDrawers();
                    return true;
                }
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    drawerLayout.closeDrawers();
                }
                return handled;
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)).getNavController();
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}