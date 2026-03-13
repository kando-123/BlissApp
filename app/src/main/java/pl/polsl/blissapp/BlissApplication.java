package pl.polsl.blissapp;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;
import pl.polsl.blissapp.data.room.BlissDatabase;

@HiltAndroidApp
public class BlissApplication extends Application
{
    private static BlissDatabase database;

    @Override
    public void onCreate()
    {
        super.onCreate();
        database = BlissDatabase.getDatabase(this);
    }

    public static BlissDatabase getDatabase()
    {
        return database;
    }
}
