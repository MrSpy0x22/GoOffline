package pl.gooffline.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.entity.Whitelist;

@Database(entities = { Whitelist.class } , version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static final String APP_DB_NAME = "app.db";
    private static AppDatabase appDatabase = null;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context , AppDatabase.class , APP_DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return appDatabase;
    }

    public abstract WhitelistDao whitelistDAO();
}
