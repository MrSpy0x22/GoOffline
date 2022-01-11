package pl.gooffline.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.gooffline.database.dao.CategoryDao;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.dao.WordbaseDao;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Config;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.database.entity.Wordbase;
import pl.gooffline.database.entity.WordsCategory;

@Database(entities = {
        Config.class , Whitelist.class , Category.class , Wordbase.class
    } , version = 3)
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

    public abstract ConfigDao configDAO();
    public abstract WhitelistDao whitelistDAO();
    public abstract WordbaseDao wordbaseDAO();
    public abstract CategoryDao categoryDAO();
}
