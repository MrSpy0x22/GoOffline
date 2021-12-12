package pl.gooffline.database.dao;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.gooffline.database.entity.Whitelist;

@Dao
public interface WhitelistDao {
    @Query("SELECT * FROM whitelist w")
    List<Whitelist> getAll();
    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateAll(List<Whitelist> whitelists);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Whitelist> whitelists);
    @Query("DELETE FROM whitelist")
    void deleteAll();
}
