package pl.polsl.blissapp.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import pl.polsl.blissapp.R;

public class ThemeManager {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String PREF_THEME_KEY = "theme";

    public static void changeTheme(Activity activity, int theme) {
        saveTheme(activity, theme);
        activity.recreate(); // Use recreate() for a smoother transition
    }

    public static void applyTheme(Activity activity) {
        activity.setTheme(getSavedTheme(activity));
    }

    private static void saveTheme(Context context, int theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(PREF_THEME_KEY, theme).apply();
    }

    public static int getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Return the saved theme, or the default theme if nothing is saved yet.
        return prefs.getInt(PREF_THEME_KEY, R.style.Theme_BlissApp);
    }
}
