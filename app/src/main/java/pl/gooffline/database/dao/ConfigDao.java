package pl.gooffline.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pl.gooffline.database.entity.Config;

@Dao
public interface ConfigDao {
    @Query("SELECT * FROM config c")
    List<Config> getAll();
    @Query("SELECT * FROM config c WHERE c.config_key = :key")
    Config getConfigByKey(String key);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Config> configs);
    @Query("DELETE FROM config")
    void deleteAll();
}
