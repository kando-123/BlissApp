package pl.polsl.blissapp.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import pl.polsl.blissapp.R;

public class ThemeManager {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String PREF_THEME_KEY = "theme_name";
    
    public static final String THEME_LAVENDER = "LAVENDER";
    public static final String THEME_OCEAN = "OCEAN";
    public static final String THEME_FOREST = "FOREST";
    public static final String THEME_SUNSET = "SUNSET";

    public static void changeTheme(Activity activity, String themeKey) {
        saveTheme(activity, themeKey);
        activity.recreate();
    }

    public static void applyTheme(Activity activity) {
        activity.setTheme(getThemeResource(activity));
    }

    private static void saveTheme(Context context, String themeKey) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_THEME_KEY, themeKey).apply();
    }

    public static String getSavedThemeKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_THEME_KEY, THEME_LAVENDER);
    }

    private static int getThemeResource(Context context) {
        String themeKey = getSavedThemeKey(context);

        switch (themeKey) {
            case THEME_OCEAN:
                return R.style.Theme_BlissApp_Ocean;
            case THEME_FOREST:
                return R.style.Theme_BlissApp_Forest;
            case THEME_SUNSET:
                return R.style.Theme_BlissApp_Sunset;
            case THEME_LAVENDER:
            default:
                return R.style.Theme_BlissApp_Lavender;
        }
    }
}
