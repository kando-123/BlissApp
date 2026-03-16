package pl.polsl.blissapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
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
public class MainActivity extends AppCompatActivity
{
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Install the splash screen before ThemeManager applies the theme
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        // 1. Enable edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Handle System Insets for the Toolbar (moves it lower)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, 0);
            return windowInsets;
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        NavigationView navigationView = findViewById(R.id.nav_view);

        // 3. Handle System Insets for the Navigation Drawer
        ViewCompat.setOnApplyWindowInsetsListener(navigationView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, 0);
            return windowInsets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null)
        {
            NavController navController = navHostFragment.getNavController();

            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.nav_writer);
            topLevelDestinations.add(R.id.nav_dictionary);
            topLevelDestinations.add(R.id.nav_alchemy);

            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                    .setOpenableLayout(drawerLayout)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            
            // Custom item selection to avoid popping the back stack for settings
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
            
            // Still call this to handle highlighting of the selected item
            NavigationUI.setupWithNavController(navigationView, navController);
            
            // Re-apply the custom listener because setupWithNavController sets its own
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_settings) {
                    // Navigate to settings without popping the back stack
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
        NavController navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
