package pl.polsl.blissapp.data.room;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import pl.polsl.blissapp.data.room.dao.AlchemyDao;
import pl.polsl.blissapp.data.room.dao.SymbolDao;
import pl.polsl.blissapp.data.room.dao.TranslationDao;
import pl.polsl.blissapp.data.room.entity.*;

@Database(
        entities =
        {
            SymbolEntity.class,
            ComponentEntity.class,
            PrimitiveEntity.class,

            CompositionEntity.class,
            DefinitionEntity.class,
            TranslationEntity.class,

            SymbolImageEntity.class,
            AlchemyProgressEntity.class
        },
        version = 4)
public abstract class BlissDatabase extends RoomDatabase
{
    private static volatile BlissDatabase INSTANCE;

//    static final Migration MIGRATION_4_5 = new Migration(4, 5)
//    {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database)
//        {
//            database.execSQL("CREATE TABLE IF NOT EXISTS `AlchemyProgress` (" +
//                    "`symbol_index` INTEGER NOT NULL, " +
//                    "`is_discovered` INTEGER NOT NULL DEFAULT 0, " +
//                    "PRIMARY KEY(`symbol_index`), " +
//                    "FOREIGN KEY(`symbol_index`) REFERENCES `Symbol`(`symbol_index`) ON UPDATE NO ACTION ON DELETE CASCADE )");
//        }
//    };

    public static BlissDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (BlissDatabase.class)
            {
                if (INSTANCE == null)
                {
                    Log.d("BlissDatabase", "Creating database");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BlissDatabase.class, "Symbol.db")
                            .createFromAsset("databases/Symbol.db")
//                            .addMigrations(MIGRATION_4_5)
                            .fallbackToDestructiveMigration(true)
                            .build();
                    Log.d("BlissDatabase", "Database created");
                }
            }
        }
        return INSTANCE;
    }

    public abstract SymbolDao symbolDao();
    public abstract TranslationDao translationDao();
    public abstract AlchemyDao alchemyDao();
}
