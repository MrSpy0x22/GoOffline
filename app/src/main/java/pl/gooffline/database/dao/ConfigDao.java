package pl.gooffline.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.gooffline.database.entity.Config;

@Dao
public interface ConfigDao {
    @Query("SELECT * FROM config c")
    List<Config> getAll();
    @Query("SELECT * FROM config c WHERE c.config_key = :key")
    Config getConfigByKey(String key);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Config configs);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAll(List<Config> configs);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Config> configs);
    @Query("DELETE FROM config")
    void deleteAll();
}
