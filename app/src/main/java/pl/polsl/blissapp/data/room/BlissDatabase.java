package pl.polsl.blissapp.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.polsl.blissapp.data.room.entity.*;

@Database(
        entities =
        {
            SymbolEntity.class,
            ComponentEntity.class,
            PrimitiveEntity.class,
            CompositionEntity.class,
            DefinitionEntity.class,
            TranslationEntity.class
        },
        version = 1)
public abstract class BlissDatabase extends RoomDatabase
{
    private static volatile BlissDatabase INSTANCE;

    //public abstract SymbolDao symbolDao();

    public static BlissDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (BlissDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BlissDatabase.class, "Symbols.db")
                            .createFromAsset("databases/Symbols.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
