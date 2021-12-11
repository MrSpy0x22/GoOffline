package pl.gooffline.database.dao;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pl.gooffline.database.entity.Whitelist;

@Dao
public interface WhitelistDao {
    @Query("SELECT w.package_name FROM whitelist w")
    List<Whitelist> getAll();
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Whitelist... whitelists);
    @Query("DELETE FROM whitelist")
    void deleteAll();
}
